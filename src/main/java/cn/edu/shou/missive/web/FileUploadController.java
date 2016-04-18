package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.PDFBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Controller
@SessionAttributes(value = {"userbase64","user_id","user"})
public class FileUploadController {
    //----------------wls------------used---------------------->>>>>>>>>start

    @Value("${spring.main.uploaddir}")
    String fileUploadDir;
    @Autowired
    MissivePublishRepository mpr;

    @Autowired
    MissiveRecSeeCardRepository mrscr;
    @Autowired
    private MissivePublishRepository mPublishR;

    @Autowired
    private UserRepository useR;
    @Autowired
    private ActivitiService actR;
    @Autowired
    private HistoryService historyService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    //    int realPathLen=58;//路径字符数量
    CommonFunction commF=new CommonFunction();

    //上传文件处理器
    @RequestMapping(value="/{fileType}/upload/{instanceID}", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("files") MultipartFile file,
                                                 @PathVariable("fileType") String fileType,
                                                 @PathVariable("instanceID")Long instanceID){
        //文件路径由公文ID和版本号组成 如果是新建 则版本号为1
        //首先根据公文ID查找当前版本最新号码

        boolean folderExit;//文件夹是否存在

        String upSuccess="true";//上传成功标志
        String newFolder=null;
        String oldFolder=null;
        String realPath=fileUploadDir+"/"+fileType+"/";


        int fileVersionNum = 0;//默认文件版本为0+1
        fileVersionNum=getMaxMissiveVersion(instanceID,fileType);////获取最新版本号
        oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";
        fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
        newFolder=realPath+instanceID+"/"+fileVersionNum+"/";
        folderExit=FileOperate.exitFolder(newFolder);
        if (!folderExit) {
            {
                //如果不存在，则先将文件夹拷贝
                FileOperate.newFolder(newFolder);//创建新版本文件夹
                if (fileVersionNum!=1) {
                    FileOperate.copyFolder(oldFolder, newFolder);//拷贝文件夹 如果是新建则不需要拷贝
                }
            }
        }else {
            //如果文件夹存在 则删除文件夹
            //FileOperate.delFolder(newFolder);//删除文件夹
            if (fileVersionNum!=1) {
                FileOperate.copyFolder(oldFolder, newFolder);//拷贝文件夹 如果是新建则不需要拷贝
            }
        }
        upSuccess=commF.uploadFiles(newFolder,file);//上传文件
        return upSuccess;
    }
    //删除文件

    @RequestMapping(value="/file/{fileType}/remove/{instanceID}", method=RequestMethod.POST,produces = "application/json")
    public @ResponseBody String handleFileRemove1(@RequestParam String[] fileNames,
                                                 @PathVariable("fileType") String fileType,
                                                 @PathVariable("instanceID")Long instanceID){
        //在删除文件之前，先判断该版本下面是否有文件，没有则新建下一个版本，把该版本下的文件夹全部拷贝到新建版本下
        //然后在新建版本下删除文件
        boolean folderExit=false;//文件夹是否存在
        boolean delFlag=true;//删除标志
        boolean copyFolderFlag=true;//拷贝文件夹成功标志
        String realPath=fileUploadDir+"/"+fileType+"/";
//        realPath=realPath.length()>realPathLen?realPath.substring(0,realPathLen):realPath;//如果字符长度大于58个字符就截取
        String fileName=fileNames[0];//删除文件名称

        int fileVersionNum = 0;//默认文件版本为0+1
        fileVersionNum=getMaxMissiveVersion(instanceID,fileType);////获取最新版本号
        String oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";//原有路径
        fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
        String newFolder=realPath+instanceID+"/"+fileVersionNum+"/";//新路径
        folderExit=FileOperate.exitFolder(newFolder);
        //如果是新建公文 则直接在文件夹下删除文件
        if (fileVersionNum!=1) {
            if (folderExit) {
                //如果存在，则查找需要删除的文件进行删除
                if (FileOperate.exitFile(newFolder + fileName)) {
                    delFlag = FileOperate.delFile(newFolder + fileName);
                } else {
                    delFlag = true;//如果文件不存在 则直接返回删除为true
                }
            } else {
                //如果不存在，则先将文件夹拷贝，然后再进行文件删除
                //先创建文件夹路径
                FileOperate.newFolder(newFolder);//创建新版本文件夹
                copyFolderFlag = FileOperate.copyFolder(oldFolder, newFolder);//拷贝文件夹
                delFlag = FileOperate.delFile(newFolder + fileName);

            }
        }else {
//            faxCableMaxID=faxR.getMaxID()+1;//获取公文ID
//            versionINum=Long.parseLong("1");//最新新建公文的版本号 默认为1
            realPath=realPath+instanceID+"/"+fileVersionNum+"/"+fileName;
            delFlag=FileOperate.delFile(realPath);
        }

        if(copyFolderFlag&&delFlag){
            return "true";
        }else {
            return "false";
        }

    }
    @RequestMapping(value="/{fileType}/remove/{instanceID}", method=RequestMethod.POST,produces = "application/json")
    public @ResponseBody String handleFileRemove(@RequestParam String[] fileNames,
                                                 @PathVariable("fileType") String fileType,
                                                 @PathVariable("instanceID")Long instanceID){


        //在删除文件之前，先判断该版本下面是否有文件，没有则新建下一个版本，把该版本下的文件夹全部拷贝到新建版本下
        //然后在新建版本下删除文件
        boolean folderExit=false;//文件夹是否存在
        boolean delFlag=true;//删除标志
        boolean copyFolderFlag=true;//拷贝文件夹成功标志
        String realPath=fileUploadDir+"/"+fileType+"/";
//        realPath=realPath.length()>realPathLen?realPath.substring(0,realPathLen):realPath;//如果字符长度大于58个字符就截取
        String fileName=fileNames[0];//删除文件名称

        int fileVersionNum = 0;//默认文件版本为0+1
        fileVersionNum=getMaxMissiveVersion(instanceID,fileType);////获取最新版本号
        String oldFolder=realPath+instanceID+"/"+fileVersionNum+"/";//原有路径
        fileVersionNum++;//如果是更新 则文件上传至下一个版本目录下
        String newFolder=realPath+instanceID+"/"+fileVersionNum+"/";//新路径
        folderExit=FileOperate.exitFolder(newFolder);
        //如果是新建公文 则直接在文件夹下删除文件
        if (fileVersionNum!=1) {
            if (folderExit) {
                //如果存在，则查找需要删除的文件进行删除
                if (FileOperate.exitFile(newFolder + fileName)) {
                    delFlag = FileOperate.delFile(newFolder + fileName);
                } else {
                    delFlag = true;//如果文件不存在 则直接返回删除为true
                }
            } else {
                //如果不存在，则先将文件夹拷贝，然后再进行文件删除
                //先创建文件夹路径
                FileOperate.newFolder(newFolder);//创建新版本文件夹
                copyFolderFlag = FileOperate.copyFolder(oldFolder, newFolder);//拷贝文件夹
                delFlag = FileOperate.delFile(newFolder + fileName);

            }
        }else {
//            faxCableMaxID=faxR.getMaxID()+1;//获取公文ID
//            versionINum=Long.parseLong("1");//最新新建公文的版本号 默认为1
            realPath=realPath+instanceID+"/"+fileVersionNum+"/"+fileName;
            delFlag=FileOperate.delFile(realPath);
        }

        if(copyFolderFlag&&delFlag){
            return "true";
        }else {
            return "false";
        }

    }

    /**
     * 文件下载
     *
     * @param fname 文件名称（含后缀）
     * @throws
     */


    @RequestMapping("/pdf/{fileType}/{instanceID}/{VersionNUM}/{fname}.{filetype}")
     public ResponseEntity<byte[]> downFile(@PathVariable String fileType,@PathVariable Long instanceID,@PathVariable Long VersionNUM,
                                            @PathVariable String fname,@PathVariable String filetype)  {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String path = req.getSession().getServletContext().getRealPath("/");
        String path = fileUploadDir+"/"+fileType+"/"+instanceID+"/"+VersionNUM+"/";
        //默认文件名称
        String downFileName = fname+"."+filetype;
        try {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
        } catch (Exception e) {
            logger.error("downFile exception:",e);
            e.printStackTrace();
        }

        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", downFileName);

        try {
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(path  + URLDecoder.decode(downFileName, "UTF-8"))),
                    headers,
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("downFile-ResponseEntity exception:",e);
            e.printStackTrace();
            //日志

        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }
    public byte[] getImgByte(InputStream is)throws IOException{
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int b;
        while((b=is.read())!=-1){
            bytestream.write(b);
        }
        byte[] bs = bytestream.toByteArray();
        bytestream.close();
        return  bs;
    }





   //by db 2014-11-26 查看我的公文
//    @RequestMapping("/pdf/{fileType}/{instanceID}/{VersionNum}/pdf2Pad/{fname}.{filetype} &name={missiveTittle}")
   @RequestMapping("/pdf/{fileType}/{instanceID}/{VersionNum}/pdf2Pad/{fname}.{filetype}")
    public void readResource(@PathVariable String fileType,@PathVariable Long instanceID,
                             @PathVariable String fname,@PathVariable String filetype,@PathVariable String VersionNum, HttpServletResponse response)
            throws Exception {


        String path = fileUploadDir+"/"+fileType+"/"+instanceID+"/"+VersionNum+"/pdf2Pad/";
//        templatesControl templatesControl=new templatesControl();
//        String missieTittle=templatesControl.getMissiveTitles(fileType,instanceID.toString()).split("\\|")[0];
        //默认文件名称
        String downFileName =path+ fname+"."+filetype;


        File file=new File(downFileName);
        if(file.exists()){
            downFileName=path+fname+"."+filetype;
        }
        else {
            //String TASKID=actR.getParentFlow(fname);
            List<HistoricTaskInstance> taskList =  this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceID.toString()).orderByHistoricTaskInstanceEndTime().desc().list();
            String TASKID=taskList.get(1).getId();
            downFileName =path+ TASKID+"."+filetype;
        }
        File pdfFile =new File(downFileName);
        InputStream is = new FileInputStream(pdfFile);
        OutputStream out = null;
        try{
            response.setContentType("Application/pdf");
            response.addHeader("Content-Disposition", "attachment;filename=123.pdf");
            out = response.getOutputStream();
            out.write(getImgByte(is));
            out.flush();
        }
        catch (Exception ex){
            logger.error("readResource exception:",ex);
        }
        finally {
            try{
                out.close();
            }
            catch (Exception e){
                logger.error("readResource-close exception:",e);
            }
        }

    }



    //查看最新任务公文
    @RequestMapping("/pdf/{fileType}/{instanceID}")
    public void downloadLastPdf(@PathVariable String fileType,@PathVariable String instanceID, HttpServletResponse response,@AuthenticationPrincipal User currentUser)
            throws Exception {

        List<HistoricTaskInstance> tasklist =  this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceID).orderByHistoricTaskInstanceEndTime().desc().list();
        //Group groupId=currentUser.getGroup();
        //List<HistoricTaskInstance> tasklist =  this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceID).taskCandidateGroup(groupId.toString()).orderByHistoricTaskInstanceEndTime().desc().list();
        String fname=tasklist.get(0).getId();
        String versionNum =String.valueOf(getMaxMissiveVersion(Long.parseLong(instanceID),fileType));

        String path = fileUploadDir+"/"+fileType+"/"+instanceID+"/"+versionNum+"/pdf2Pad/";
        //默认文件名称
        String downFileName =path+ fname+".pdf";
        File pdfFile =new File(downFileName);
        InputStream is = new FileInputStream(pdfFile);
        OutputStream out = null;
        try{
            response.setContentType("Application/pdf");

            out = response.getOutputStream();

            out.write(getImgByte(is));
            out.flush();
        }
        catch (Exception ex){
            logger.error("readResource exception:",ex);


        }
        finally {
            try{
                out.close();
            }
            catch (Exception e){
                logger.error("readResource-close exception:",e);

            }
        }

    }
    /*public ResponseEntity<byte[]> downPdfFile(@PathVariable String fileType,@PathVariable Long instanceID,
                                           @PathVariable String fname,@PathVariable String filetype,@PathVariable String VersionNum)  {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String path = fileUploadDir+"/"+fileType+"/"+instanceID+"/"+VersionNum+"/pdf2Pad/";
        //默认文件名称
        String downFileName = fname+"."+filetype;
      try {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
        } catch (Exception e) {
            logger.error("downPdfFile exception:",e);

            e.printStackTrace();
        }

        //Http响应头
        HttpHeaders headers = new HttpHeaders();


        try {
            String file = path  + URLDecoder.decode(downFileName, "UTF-8");

            if(FileOperate.exitFile(file)) {
                //headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", downFileName);
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(file)),
                        headers,
                        HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.error("downPdfFile-Response exception:", e);

        }
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }*/

    //根据流程号查询公文
    @RequestMapping("/pdf/{missiveType}/pdf2Pad/{instanceID}")
    public ResponseEntity<byte[]> downPdfFileByInstanceId(@PathVariable String missiveType,@PathVariable String instanceID){
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String path = req.getSession().getServletContext().getRealPath("/");
        String versionNum =String.valueOf(getMaxMissiveVersion(Long.parseLong(instanceID),missiveType));
        String path = fileUploadDir+"/"+missiveType+"/"+instanceID+"/"+versionNum+"/"+"pdf2Pad/";
        //默认文件名称
        Task currentTask  = actR.getCurrentTasksByProcessInstanceId(Long.parseLong(instanceID));
        String previousTaskId="";
        if(currentTask!=null){
             previousTaskId =actR.getPreviousTaskIDByCurrentTaskID(currentTask.getId());

        }else{
            List<HistoricTaskInstance> previousTasklist = this.historyService.createHistoricTaskInstanceQuery().processInstanceId(instanceID).orderByHistoricTaskInstanceEndTime().desc().list();

            //return previousTasklist.get(0).getId();
            if(!previousTasklist.isEmpty()) {
                previousTaskId= previousTasklist.get(0).getId();
            }

        }

        String downFileName = previousTaskId+".pdf";
        try {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
        } catch (Exception e) {
            logger.error("downPdfFileByInstanceId exception:",e);

            e.printStackTrace();
        }

        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", downFileName);

        if (FileOperate.exitFile(path+"/"+downFileName))
        {
            try {
                return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(path  + URLDecoder.decode(downFileName, "UTF-8"))),
                        headers,
                        HttpStatus.OK);
            } catch (Exception e) {
                logger.error("downPdfFileByInstanceId-Response exception:",e);
                e.printStackTrace();
                //日志

            }
        }else
        {
            headers.setContentType(MediaType.TEXT_HTML);
            //headers.setContentDispositionFormData("attachment", "error.txt");
            return new ResponseEntity<byte[]>("".getBytes(), headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<byte[]>("".getBytes(), headers, HttpStatus.NOT_FOUND);
    }

    //pdf文件下载 by db 2014-10-22 供移动端下载
    @RequestMapping("/download/pdf/{fileType}/{instanceID}/{fname}.{filetype}")
    public ResponseEntity<byte[]> downPDFFile(@PathVariable String fileType,@PathVariable Long instanceID,
                                           @PathVariable String fname,@PathVariable String filetype)  {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String path = req.getSession().getServletContext().getRealPath("/");
        String versionNum =String.valueOf(getMaxMissiveVersion(instanceID,fileType));
        String path = fileUploadDir+"/"+fileType+"/"+instanceID+"/"+versionNum+"/"+"pdf2Pad/";
        //默认文件名称
        String downFileName = fname+"."+filetype;
        try {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
        } catch (Exception e) {
            logger.error("downPDFFile exception:",e);

            e.printStackTrace();
        }

        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", downFileName);

       if (FileOperate.exitFile(path+"/"+downFileName))
       {
           try {
               return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(path  + URLDecoder.decode(downFileName, "UTF-8"))),
                       headers,
                       HttpStatus.OK);
           } catch (Exception e) {
               logger.error("downPDFFile-Response exception:",e);
               e.printStackTrace();
               //日志

           }
       }else
       {
           headers.setContentType(MediaType.TEXT_HTML);
           //headers.setContentDispositionFormData("attachment", "error.txt");
           return new ResponseEntity<byte[]>("".getBytes(), headers, HttpStatus.NOT_FOUND);
       }
        return new ResponseEntity<byte[]>("".getBytes(), headers, HttpStatus.NOT_FOUND);
    }


    //pdf文件下载 by db 2014-10-22 供移动端下载
    @RequestMapping("/download/pdf/{fileType}/{instanceID}/{fname}.{filetype}/exists")
    public @ResponseBody String existsPDFFile(@PathVariable String fileType,@PathVariable Long instanceID,
                                              @PathVariable String fname,@PathVariable String filetype)  {

        String versionNum =String.valueOf(getMaxMissiveVersion(instanceID,fileType));
        String path = fileUploadDir+"/"+fileType+"/"+instanceID+"/"+versionNum+"/"+"pdf2Pad/";
        //默认文件名称
        String downFileName = fname+"."+filetype;


        if (FileOperate.exitFile(path+"/"+downFileName))
        {
            return "ok";
        }else
        {
           return "not exists";
        }

    }

    //移动端上传图片文件
    @RequestMapping("upload/img/{fileType}/{processDeId}/{instanceId}/{VersionNum}/{taskId}")
    public @ResponseBody String ipadUploadImg(@RequestParam("files") MultipartFile file,@PathVariable String fileType,@PathVariable String processDeId,@PathVariable Long instanceId,@PathVariable Long VersionNum,@PathVariable Long taskId){
        String imgUploadDir=this.fileUploadDir+"/"+fileType+"/"+instanceId+"/"+VersionNum+"/pdf2Pad/";//图片文件存放地址
        boolean isFileExist = FileOperate.exitFolder(imgUploadDir);
        String result="";
        commF.uploadFiles(imgUploadDir,file);
        String exsImg = imgUploadDir+taskId+".png";//新上传的png图片
        String aftImg = imgUploadDir+taskId+"new.png";//合并后的png图片
        if(processDeId.contains("ReceiptId")) {
            MissiveRecSeeCard mrsc =mrscr.getMissData(String.valueOf(instanceId));
            String preImg = mrsc.getBgPngPath();
            if(preImg!=null&&!preImg.equals("")) {
                ImageMerge.merge(exsImg, preImg, aftImg);
                mrsc.setBgPngPath(aftImg);
            }
            else{
                mrsc.setBgPngPath(exsImg);
            }
            mrscr.save(mrsc);
        }
        else if(processDeId.contains("PublishMissiveId")) {
            MissivePublish mp = mPublishR.findByProcessID(instanceId);
            String preImg=mp.getBackgroudImage();
            if(preImg!=null&&!preImg.equals("")) {
                ImageMerge.merge(exsImg, preImg, aftImg);
                mp.setBackgroudImage(aftImg);
            }
            else{
                mp.setBackgroudImage(exsImg);
            }
            mPublishR.save(mp);
        }

       return "ok";
    }
    /**
     * 创建存放上传文件的文件夹，如果不存在则自动创建
     * @param realPath
     * @param uploadDate
     * @return
     */
    public String createFolder(String realPath,String uploadDate){
        String currFoder = "upload/"+uploadDate;
        String fileFoder = realPath+currFoder;


        FileOperate op = new FileOperate();
        op.newFolder(fileFoder);    //调用newFolder()方法创建文件夹
        return currFoder;
    }

    //获取最新版本号
    //todo use hql fix this
    public  int getMaxMissiveVersion(Long instanceID,String fileType){
        int fileVersionNum = 0;//默认文件版本为0+1
        if(fileType.equals("missivePublish")){
            MissivePublish missiveForm=new MissivePublish();
            missiveForm = mpr.findByProcessID(instanceID);//获取最大版本号
            if(missiveForm!=null){
                if(missiveForm.getMissiveInfo()!=null){
                    if(missiveForm.getMissiveInfo().getVersions()!=null){
                        List<MissiveVersion> missiveVersions = missiveForm.getMissiveInfo().getVersions();
                        if(missiveForm.getMissiveInfo().getVersions().size()!=0){
                            List<Long> mvnum=new ArrayList<Long>();
                            for(MissiveVersion mv:missiveVersions){
                                mvnum.add(mv.getVersionNumber());
                            }
                            fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                        }
                    }
                }
            }
            return fileVersionNum;
        }

        else if(fileType.equals("missiveReceive")){
            MissiveRecSeeCard mrsc=mrscr.getMissData(String.valueOf(instanceID));
            if(mrsc!=null){
                if(mrsc.getMissiveInfo()!=null){
                    List<MissiveVersion> missiveVersions = mrsc.getMissiveInfo().getVersions();
                    if(mrsc.getMissiveInfo().getVersions().size()!=0){
                        List<Long> mvnum=new ArrayList<Long>();
                        for(MissiveVersion mv:missiveVersions){
                            mvnum.add(mv.getVersionNumber());
                        }
                        fileVersionNum= Integer.parseInt(Collections.max(mvnum).toString());//获取最新版本号
                    }
                }
            }
            return fileVersionNum;
        }


        else return 0;
    }
    //----------------wls------------used---------------------->>>>>>>>>end




   @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(
                                                 @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            String name = file.getOriginalFilename();
            try {

                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
            } catch (Exception e) {
                logger.error("handleFileUpload exception:",e);

                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload  because the file was empty.";
        }
    }



    @RequestMapping("/pdf/{fname}.{filetype}")
    public ResponseEntity<byte[]> downFile(@PathVariable String fname,@PathVariable String filetype)  {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //String path = req.getSession().getServletContext().getRealPath("/");
        String path = "/Users/sqhe/Documents/Projects/esicmissive_springboot";
        //默认文件名称
        String downFileName = fname+"."+filetype;
        try {
            downFileName = URLEncoder.encode(downFileName, "UTF-8");//转码解决IE下文件名乱码问题
        } catch (Exception e) {
            logger.error("downFile exception:",e);

            e.printStackTrace();
        }

        //Http响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", downFileName);

        try {
            return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(path + "/" + downFileName)),
                    headers,
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("downFile-Response exception:",e);

            e.printStackTrace();
            //日志

        }

        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "error.txt");
        return new ResponseEntity<byte[]>("文件不存在.".getBytes(), headers, HttpStatus.OK);
    }


    //FileOperate fileOperate=new FileOperate();

    /*@RequestMapping(value="/upload/{id}", method=RequestMethod.POST)
    @ResponseBody
    public String handleImageUpload(
            @RequestParam("files") MultipartFile file,@PathVariable Long id){
        if (!file.isEmpty()) {

            String rel_path=fileUploadDir+"/userImg/"+id;  //用户的头像上传到到该文件夹

            //String rel_path1="/userImg/"+id;  //用户的头像上传到到该文件夹
            try {
                FileUtils.forceMkdir(new File(rel_path));
            } catch (IOException e) {
                logger.error("handleImageUploadIO exception:",e);

                e.printStackTrace();
            }
            //fileOperate.newFolder(rel_path);


            String name =rel_path+"/"+file.getOriginalFilename();

            try {
                FileUtils.cleanDirectory(new File(rel_path));
            } catch (IOException e) {
                logger.error("handleImageUpload IOexception:",e);
                e.printStackTrace();
            }
            //String name1=rel_path1+"/"+ file.getOriginalFilename();

            try {

                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                //return "true";   //上传成功之后返回
                return "{\"passed\":\""+name+"\"}";
            } catch (Exception e) {
                logger.error("handleImageUploadFile exception:",e);

                return "false";
            }
        } else {
            return "false";
        }
    }*/
    @RequestMapping(value="/uploadSign/{id}", method=RequestMethod.POST)
    @ResponseBody
    public boolean handleImageUploadSign(
            @RequestParam("files") MultipartFile file,@PathVariable Long id){
        try {
            byte[] bytes = file.getBytes();//获取签名字符流
            //将获取的签名字符流存入用户数据表
            User userInfo=useR.findOne(id);
            if (userInfo!=null){
                userInfo.setSignImg(bytes);//将签名插入数据库
                useR.save(userInfo);//保存操作
                return true;
            }else {
                return false;
            }

        } catch (Exception e) {
            logger.error("handleImageUploadSign exception:",e);

            return false;
        }
    }
    //01-28孙乐 上传反馈图片
    @RequestMapping(value="/feedbackupload/{id}", method=RequestMethod.POST)
    @ResponseBody
    public String ImageUpload(
            @RequestParam("files") MultipartFile file,@PathVariable Long id) throws IOException {

        if (!file.isEmpty()) {
            String rel_path=fileUploadDir+"/feedback/"+id;  //用户的头像上传到到该文件夹

            //String rel_path1="/userImg/"+id;  //用户的头像上传到到该文件夹
            try {
                FileUtils.forceMkdir(new File(rel_path));
            } catch (IOException e) {
                logger.error("handleImageUploadIO exception:",e);

                e.printStackTrace();
            }
            String name =rel_path+"/"+file.getOriginalFilename();
            try {

                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                //return "true";   //上传成功之后返回
                return "{\"passed\":\""+name+"\"}";
            } catch (Exception e) {
                logger.error("handleImageUploadFile exception:",e);

                return "false";
            }
        } else {
            return "false";
        }

    }


    @RequestMapping(value="/getSign/{id}", method=RequestMethod.GET)
    @ResponseBody
    public Image handleImageGetSign
            (@PathVariable Long id){
        try {

            //将获取的签名字符流存入用户数据表
            User userInfo=useR.findOne(id);
            if (userInfo!=null){
                byte[] bytes = userInfo.getSignImg();//获取签名字符流

                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpg");

                //ImageIO is a class containing static methods for locating ImageReaders
                //and ImageWriters, and performing simple encoding and decoding.

                ImageReader reader = (ImageReader) readers.next();
                Object source = bis;
                ImageInputStream iis = ImageIO.createImageInputStream(source);
                reader.setInput(iis, true);
                ImageReadParam param = reader.getDefaultReadParam();
                Image image = reader.read(0, param);

                return image;
            }else {
                return null;
            }

        } catch (Exception e) {
            logger.error("handleImageGetSign exception:",e);
            return null;
        }
    }


    @RequestMapping(value="/upload/{id}", method=RequestMethod.POST)
    @ResponseBody
    public String handleImageUpload(
            @RequestParam("files") MultipartFile file,@PathVariable Long id){
        if (!file.isEmpty()) {

            String rel_path=fileUploadDir+"/userImg/"+id;  //用户的头像上传到到该文件夹
            String rel_path2=fileUploadDir+"/thumbnail/"+id;  //用户的头像上传到到该文件夹

            try {
                FileUtils.forceMkdir(new File(rel_path));
                FileUtils.forceMkdir(new File(rel_path2));

            } catch (IOException e) {
                logger.error("handleImageUploadIO exception:",e);

                e.printStackTrace();
            }


            String name =rel_path+"/"+file.getOriginalFilename();
            String name2 =rel_path2+"/"+file.getOriginalFilename();


            try {
                FileUtils.cleanDirectory(new File(rel_path));
                FileUtils.cleanDirectory(new File(rel_path2));

            } catch (IOException e) {
                logger.error("handleImageUpload IOexception:",e);
                e.printStackTrace();
            }

            try {

                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name)));
                stream.write(bytes);
                stream.close();
                BufferedImage input = ImageIO.read(new File(name));
                Image big = input.getScaledInstance(200, 200,Image.SCALE_DEFAULT);
                BufferedImage inputbig = new BufferedImage(250, 250,BufferedImage.TYPE_INT_BGR);
                inputbig.getGraphics().drawImage(input, 0, 0, 250, 250, null);
                ImageIO.write(inputbig,"png", new File(name2));


                //return "true";   //上传成功之后返回
                return "{\"passed\":\""+name+"\"}";
            } catch (Exception e) {
                logger.error("handleImageUploadFile exception:",e);

                return "false";
            }
        } else {
            return "false";
        }
    }


}






