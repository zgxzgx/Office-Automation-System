package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.domain.missiveDataForm.MissivePublishForm;

import cn.edu.shou.missive.domain.missiveDataForm.TaskForm;
import cn.edu.shou.missive.service.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;



import java.io.FileOutputStream;
import java.io.*;
import java.util.*;
import java.util.List;


/**
 * Created by seky on 14-8-3.
 * 放置一些常用处理函数
 */
@Component
public class CommonFunction {
    //获取系统当前日期

    //转换工具路径

    @Value("${spring.main.convertTool}")
    String convertTool;
    @Value("${spring.main.uploaddir}")
    String uploaddir;
    @Value("${spring.main.pdfBoxTool}")
    String pdfBoxTool;
    @Autowired
    private AttachmentRepository actR;
    @Autowired
    private MissiveVersionRepository mvr;
    @Autowired
    private PdfService pdfService;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MissivePublishRepository mpDAO;
    private final static Logger logger = LoggerFactory.getLogger(CommonFunction.class);

//    public Date getCurrentDate(){
//
//        Date now=new Date();
//
//        return now;
//    }
    //根据object返回带ID的Json数据
    public String getJsonDataByObject(Object obj){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        String json = null;
        try {
            json = mapper.writeValueAsString(obj);
        } catch (IOException e) {
            logger.error("getJsonDataByObject exception:",e);
            e.printStackTrace();
        }
        return json;
    }
    //根据对象数据返回数据值
    public  String getStrByObject(Object[] obj){
        String str="";
        for (int i=0;i<obj.length;++i){
                str+=((Group) obj[i]).id+",";//获取对象值
        }
        //去除最后一个,
        str=str.length()==0?"":str.substring(0,str.length()-1);

        return str;
    }
    //根据对象数据返回数据值
    public  String getNameStrByObject(Object[] obj){
        String str="";
        for (int i=0;i<obj.length;++i){
            str+=((Group) obj[i]).groupName+",";//获取对象值
        }
        //去除最后一个,
        str=str.length()==0?"":str.substring(0,str.length()-1);

        return str;
    }
    //根据对象数据返回数据值
    public  String getStaticStrByObject(Object[] obj){
        String str="";
        for (int i=0;i<obj.length;++i){
            str+=((Group) obj[i]).groupName+",";//获取对象值
        }
        //去除最后一个,
        str=str.length()==0?"":str.substring(0,str.length()-1);

        return str;
    }
    //根据对象数据返回名称
//    public  String getNameByObject(Object[] obj){
//        String str="";
//        for (int i=0;i<obj.length;++i){
//            str+=((Group) obj[i]).groupName+",";//获取对象值
//        }
//        //去除最后一个,
//        str=str.substring(0,str.length()-1);
//
//        return str;
//    }
    //上传文件
    public String uploadFiles(String realPath,MultipartFile file){
        //首先判断文件夹是否存在 不存在创建
        FileOperate.newFolder(realPath);
        if (!file.isEmpty()) {

            String name = file.getOriginalFilename();
            //去掉文件名中的空格
            //name=name.replace(" ","");
            name = FilenameUtils.getName(name);
            String fileName=realPath+name;
            try {

                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(fileName)));
                stream.write(bytes);
                stream.close();
                return "true";
            } catch (Exception e) {
                logger.error("uploadFiles exception:",e);
                return "false";
            }
        } else {
            return "false";
        }
    }
    //调用exe将office转换成pdf
//    public String officeToPDF(String exePath,String sourceFile,String targetFile){
//        //ExecuteShellComand obj = new ExecuteShellComand();
//        String domainName = "google.com";
//        //String sourceFile = "C:\\Users\\XQQ\\Desktop\\123.docx ";//源文件 包含路径
//        //String targetFile="D:\\123.pdf";//目标文件 包含路径
//
//        //in mac oxs
//        String command = "ping -c 3 " + domainName;
//
//        //in windows
//        //String command="C:\\Users\\XQQ\\Desktop\\word2pdf816.exe"+sourceFile+targetFile;
//        //String command=exePath+sourceFile+targetFile;
//
//        String output = this.executeCommand(command);
//        return output;
//    }
    //调用外部执行html转pdf
//    public String htmlToPDF(String sourceFile,String targetFile){
//        String command="wkhtmltopdf "+sourceFile+" "+targetFile;
//        String output = this.executeCommand(command);
//        return output;
//    }
//    //执行外部应用程序
//    public String executeCommand(String command) {
//
//        StringBuffer output = new StringBuffer();
//
//        Process p;
//        try {
//            p = Runtime.getRuntime().exec(command);
//            p.waitFor();
//            BufferedReader reader =
//                    new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//            String line = "";
//            while ((line = reader.readLine())!= null) {
//                output.append(line + "\n");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return output.toString();
//
//    }
//    public String shellOption(String htmlURL,String pdfURL) {
//        //ExecuteShellComand obj = new ExecuteShellComand();
//        String htmlpath=htmlURL;
//        String pdfpath=pdfURL;
//        String command  = "wkhtmltopdf -T 0 -B 0 -L 0 -R 0";
//        command+=" "+htmlpath+" "+pdfpath;
////        command="wkhtmltopdf -T 0 -B 0 -L 0 -R 0 http://localhost:8888/missiveSign/missiveSignToPDF/2522 /Users/sqhe18/Desktop/missive2.pdf";
//        String output = this.executeCommand(command);
//        return output;
//    }
    public String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
//            p.waitFor();
//            BufferedReader reader =
//                    new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//            String line = "";
//            while ((line = reader.readLine())!= null) {
//                output.append(line + "\n");
//            }

        } catch (Exception e) {
            logger.error("executeCommand exception:",e);
            e.printStackTrace();
        }

        return "OK";

    }
    //图片转换pdf
    //图片包括路径
//    public boolean imgToPDF(String imgPath,String pdfPath)
//    {
//        try{
//            //Create Document Object
//            Document convertJpgToPdf=new Document(PageSize.A4, 0, 0, 0, 0);
//            int indentation = 0;
//            //Create PdfWriter for Document to hold physical file
//            PdfWriter.getInstance(convertJpgToPdf, new FileOutputStream(pdfPath));
//            convertJpgToPdf.open();
//            //Get the input image to Convert to PDF
//            Image convertJpg=Image.getInstance(imgPath);
//            float scaler = ((convertJpgToPdf.getPageSize().getWidth() - convertJpgToPdf.leftMargin()
//                    - convertJpgToPdf.rightMargin() - indentation) / convertJpg.getWidth()) * 100;
//            convertJpg.scalePercent(scaler);
//            //Add image to Document
//            convertJpgToPdf.add(convertJpg);
//            //Close Document
//            convertJpgToPdf.close();
//            System.out.println("Successfully Converted JPG to PDF in iText");
//            return true;
//        }
//        catch (Exception e){
//            logger.error("imgToPDF exception:",e);
//            e.printStackTrace();
//            return false;
//        }
//    }


    //转换并合并pdf版本2 by db 2014-11-12

    /**
     *
     * @param taskName 当前任务名
     * @param needTaskName 需要进行附件转成pdf的任务名
     * @param LastVersionID 最新公文版本ID
     * @param latestVersion 公文最新版本号
     * @param attachs 最新版本的附件路径数组
     * @param instanceId 流程实例编号
     * @param taskId 当前任务编号
     * @param htmlUrl 静态网页地址
     * @param processType 流程类型
     */
    public void convertAtt2Pdf2(String taskName,String needTaskName,String LastVersionID,Long latestVersion,String[] attachs, Long instanceId,Long taskId,String htmlUrl,String processType){

        String uploadPath=uploaddir+"/"+processType+"/";//不同流程上传路径
        String fileBasePath=uploadPath+instanceId+"/"+latestVersion+"/";//版本路径
        //MissiveVersion mVersion = mvr.findById(Long.parseLong(LastVersionID));
        MissiveVersion mVersion = mvr.findOne(Long.parseLong(LastVersionID));
        List<Attachment> attachList = mVersion.getAttachments();

        //收文表单转换成pdf

        String pdfPath=fileBasePath+"html2pdf/";
        String pdf=pdfPath+taskId+".pdf";
        if(!FileOperate.exitFolder(pdfPath)){
            FileOperate.newFolder(pdfPath);
        }
        logger.info("begin convert html:");
//        if(processType=="faxCablePublish"){
//
//            ConvertPdfForFax(htmlUrl, pdf, "wkhtmltopdf");//静态网页转换成pdf,移动端适用
//        }else{
//            ConvertPdf(htmlUrl, pdf, "wkhtmltopdf");//静态网页转换成pdf,移动端适用
//        }

        ConvertPdf(htmlUrl, pdf, "wkhtmltopdf");//静态网页转换成pdf,移动端适用

        logger.info("end convert html:");

        logger.info("begin convert pdf:");
        //附件转换成pdf
        ArrayList<String> pdf4Merge = new ArrayList<String>();
        pdf4Merge.add(pdf);//第一个pdf必须是表单，而不是附件

        //去除同名文件的pdf
        ArrayList<String> attNew =new ArrayList<String>();
        if(attachs!=null) {
            for (String att : attachs) {
                attNew.add(att);
            }
        }
        //ArrayList<String>attNew =delSameNamePdf(atts);


        if(attNew.size()!=0) {//附件转换只在某些任务中进行
            java.util.List<String> suffixArr=new ArrayList<String>();
            suffixArr.add("doc");
            suffixArr.add("docx");
            suffixArr.add("ppt");
            suffixArr.add("pptx");
            suffixArr.add("xls");
            suffixArr.add("xlsx");
            for (int i=0;i<attNew.size();i++) {
                String titleWithPath = attNew.get(i);//getAttachmentTittle();
                String attachSuffix=titleWithPath.substring(titleWithPath.lastIndexOf(".")+1,titleWithPath.length());
                //保存由附件生成的pdf
                if(suffixArr.contains(attachSuffix)) {
                    String attachFile = titleWithPath.substring(0, titleWithPath.lastIndexOf("."));
                    String attachFileName=titleWithPath.substring(titleWithPath.lastIndexOf("/")+1,titleWithPath.lastIndexOf("."));

                    //String attachFile = fileBasePath + title;
                    String attachPdf = attachFile + ".pdf";
                    String attachpdfName = attachFileName+".pdf";
                    //防止重复转换
                      //if(!FileOperate.exitFile(attachPdf)) {

//                    if((taskName.equals("文印室套红排版打印")&&processType.equals("MissivePublish"))||
//                    (taskName.equals("处室拟稿")&&processType.equals("MissivePublish"))||
//                    (taskName.equals("处室拟稿")&&processType.equals("MissiveSign"))||
//                    (taskName.equals("办公室登记")&&processType.equals("MissiveReceive"))){
                        ConvertPdf(titleWithPath.replace("/","\\"), attachPdf.replace("/","\\"), convertTool.replace("/","\\"));
//                    }

                    String attachpdfNameNew = attachpdfName.replace(" ","%20");
                    if(FileOperate.exitFile(fileBasePath+attachpdfNameNew)&&attachpdfNameNew.contains("%20")){
                        FileOperate.moveFile(fileBasePath,fileBasePath,attachpdfNameNew,attachpdfName);
                    }
                      //}

                          //pdf信息存入公文附件中，防止重复上传附件信息
                      ArrayList<String> attFiles = actR.getAttachmentByVersionId(Long.parseLong(LastVersionID));

                      if(!attFiles.contains(attachpdfName)||attFiles==null) {
                          if(FileOperate.exitFolder(attachPdf)) {//判断是否存在对应的转换pdf

                              jdbc.update("insert into attachment(is_original_file,attachment_tittle,attachment_file_path,miisive_version_id)" +
                                      "VALUES (false,'"+attachpdfName+"','"+attachPdf+"','"+mVersion.getId()+"')");
                             /* Attachment actPdf = new Attachment();
                              actPdf.setOriginalFile(false);
                              actPdf.setAttachmentTittle(attachpdfName);
                              actPdf.setAttachmentFilePath(attachPdf);
                              actPdf.setMissiveVersion(mVersion);
                              attachList.add(actPdf);
                              mVersion.setAttachments(attachList);
                              actR.save(actPdf);*/
                              //actl.add(actPdf);
                              pdf4Merge.add(attachPdf);
                          }
                      }




                }
                else if(attachSuffix.equals("pdf")||attachSuffix.equals("PDF")){
                    pdf4Merge.add(attachs[i]);
                }
                else if(attachSuffix.toLowerCase().equals("jpg")||attachSuffix.toLowerCase().equals("png")||attachSuffix.toLowerCase().equals("gif")){
                  String imgpdfPath=fileBasePath+"html2pdf/";
                  String imgtopdf=imgpdfPath+taskId+"img"+i+".pdf";
                  this.pdfService.convertImageToPdf(attachs[i],imgtopdf);
                    pdf4Merge.add(imgtopdf);

                }

            }
        }
        else if(attachs!=null){
            for(String attPdf:attachs){
                String attachSuffix=attPdf.substring(attPdf.lastIndexOf(".")+1,attPdf.length());
                if(attachSuffix.equals("pdf")||attachSuffix.equals("PDF")){
                    pdf4Merge.add(attPdf);
                }
            }
        }
        logger.info("end convert pdf:");
        logger.info("begin merge pdf:");
        //合并pdf
        mergePdfs(pdf4Merge, instanceId, latestVersion, taskId,processType);//合并pdf
        logger.info("end merge pdf:");
    }

    //合并pdf
    public void mergePdfs(ArrayList<String> webPdfs,@PathVariable Long instanceId,@PathVariable Long versionId,@PathVariable Long taskId,String processType ) {
        PDFMergerUtility ut = new PDFMergerUtility();
        String padPdfDir=uploaddir+"/"+processType+"/"+instanceId+"/"+versionId+"/"+"pdf2Pad/";
        String output="";
        output=padPdfDir+taskId+".pdf";
        if(!FileOperate.exitFolder(padPdfDir)){//判断文件夹是否存在
            FileOperate.newFolder(padPdfDir);
        }
        if(processType=="faxCablePublish"&&webPdfs.size()>=2){

            ConvertPdfForFax(webPdfs.get(0),webPdfs.get(1),output);
            ut.addSource(output);
            if(webPdfs.size()-1>=2) {
                for (int i = 2; i < webPdfs.size() - 1; i++) {
                    ut.addSource(webPdfs.get(i));

                }
            }

        }else{
              for(String webPdf:webPdfs) {
                    ut.addSource(webPdf);
              }
        }

//        for(String webPdf:webPdfs) {
//            ut.addSource(webPdf);
//        }
        ut.setDestinationFileName(padPdfDir+taskId+".pdf");
        try {
            ut.mergeDocuments();
        } catch (IOException e) {
            logger.error("mergePdfs IOexception:",e);
            e.printStackTrace();
        } catch (COSVisitorException e) {
            logger.error("mergePdfs COSexception:",e);
            e.printStackTrace();
        }
    }

//    public void mergePdfs(ArrayList<String> webPdfs,@PathVariable Long instanceId,@PathVariable Long versionId,@PathVariable Long taskId,String processType ) {
//        PDFMergerUtility ut = new PDFMergerUtility();
//        String padPdfDir=uploaddir+"/"+processType+"/"+instanceId+"/"+versionId+"/"+"pdf2Pad/";
//        if(!FileOperate.exitFolder(padPdfDir)){//判断文件夹是否存在
//            FileOperate.newFolder(padPdfDir);
//        }
//        for(String webPdf:webPdfs) {
//            ut.addSource(webPdf);
//        }
//        ut.setDestinationFileName(padPdfDir+taskId+".pdf");
//        try {
//            ut.mergeDocuments();
//        } catch (IOException e) {
//            logger.error("mergePdfs IOexception:",e);
//            e.printStackTrace();
//        } catch (COSVisitorException e) {
//            logger.error("mergePdfs COSexception:",e);
//            e.printStackTrace();
//        }
//    }
    //转换成pdf
    /*@RequestMapping(value="/missiveReceive/html2pdf/", method=RequestMethod.GET)*/
    public String  ConvertPdf(String sourceFile,String pdf,String convertTool) {
        //ExecuteShellComand obj = new ExecuteShellComand();

        //in mac oxs
        String command="";
        String output="";
        if(convertTool.contains("wkhtmltopdf")){
            logger.info("begin html ");
            command=convertTool + " -B 0 -L 0 -R 0 -T 0 " +sourceFile+ " " + pdf;
        }
        else {//if(convertTool.contains("word2pdf816")){
            logger.info("begin pdf ");
            command=convertTool + "   \"" + sourceFile + "\"  \"" +pdf+"\""; //文件名带有空格也可以转换
        }
        output=this.executeCommands(command);
        logger.info("end html ");
        return output;

    }
    public String  ConvertPdfForFax(String sourceFile,String pdf,String outfile) {
        //ExecuteShellComand obj = new ExecuteShellComand();

        //in mac oxs
        String command="";
        String output="";


            logger.info("begin overlay ");
            //outfile=sourceFile.substring(0,sourceFile.length()-4)+"1.pdf";

          if(pdf.equals("")){
              command=convertTool + "   \"" + sourceFile + "\"  \"" +outfile+"\""; //文件名带有空格也可以转换
          }else {

              command = "java -jar " + pdfBoxTool + " OverlayPDF " + pdf + " -first " + sourceFile + " -position foreground " + outfile;
          }

//        C:\Program Files\wkhtmltopdf>java -jar pdfbox-app-1.8.9.jar OverlayPDF overlay.p
//        df -first 80015.pdf -position foreground  output4.pdf
        output=this.executeCommands(command);
        logger.info("end overlay ");
        return outfile;

    }

    //根据组名获取用户可查阅的公文
    public ArrayList<TaskForm> getMissiveLookByGroup(Group gp){
        ArrayList<TaskForm> atf = new ArrayList<TaskForm>();
        //查询传真电报的主送和抄送


        //查询发文的主送和抄送



        return atf;
    }


    public String  SplitPdf(String sourceFile) {

        String command="";

        logger.info("begin splitPdf ");
            command ="java -jar " + pdfBoxTool + " PDFSplit "   + " -startPage "  + 2  +" "  + sourceFile ;
            this.executeCommands(command);
        logger.info("end splitPdf ");
        return "";

    }


    private String executeCommands(String command) {


        StringBuffer output = new StringBuffer();

        Process p;
        try {
            //throw new Exception();
            p = Runtime.getRuntime().exec(command);
            logger.info("begin wait() ");
            p.waitFor();
            logger.info("end wait() ");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            logger.error("executeCommands exception:",e);
            e.printStackTrace();
        }

        return "";

    }

    public ArrayList<String> delSameNamePdf(ArrayList<String> sourceFiles){

        if(sourceFiles!=null&&sourceFiles.size()>1){
            for(int i=0;i<sourceFiles.size();i++){
                String fileNameI = sourceFiles.get(i).substring(0, sourceFiles.get(i).lastIndexOf("."));
                String suffixI = sourceFiles.get(i).substring(sourceFiles.get(i).lastIndexOf(".") + 1, sourceFiles.get(i).length());
                for(int j=i+1;j<sourceFiles.size();j++){
                    String fileNameJ = sourceFiles.get(j).substring(0, sourceFiles.get(j).lastIndexOf("."));
                    String suffixJ = sourceFiles.get(j).substring(sourceFiles.get(j).lastIndexOf(".") + 1, sourceFiles.get(j).length());
                    boolean flag1=fileNameI.equals(fileNameJ);
                    boolean flag2=suffixI.toLowerCase().equals("pdf")||suffixJ.toLowerCase().equals("pdf");
                    if(flag1&&flag2){
                        int index =(suffixI.toLowerCase().equals("pdf"))?i:j;
                        sourceFiles.remove(index);
                    }
                }
            }
        }
        return sourceFiles;
    }

    public Long getLatestVersion(Missive missive){
        Long latestVersion = 0l;
        if(missive!=null) {
            if (missive.getVersions() != null) {
                java.util.List<MissiveVersion> missiveVersions = missive.getVersions();
                if (missive.getVersions().size() != 0) {
                    java.util.List<Long> mvnum = new ArrayList<Long>();
                    for (MissiveVersion mv : missiveVersions) {
                        mvnum.add(mv.getVersionNumber());
                    }
                    latestVersion = Collections.max(mvnum);//获取最新版本号
                }
            }
        }
        return latestVersion;
    }
    //根据ID获取信息

    //根据附件列表和最新版本号，返回对象
    public String[] getAttAndLastVersion(String AttPath,String AttrNameList,String versionNum,String processID){

        String[] returnAttList=null;
        if (AttrNameList!=null && !AttrNameList.equals(""))
        {
            returnAttList=AttrNameList.split(",");
            for (int i=0;i<returnAttList.length;++i)
            {
                returnAttList[i]=AttPath+processID+"/"+versionNum+"/"+returnAttList[i];//将路径和版本号传入数组中
            }
        }
        return returnAttList;
    }

    public ArrayList<String> getAssigneeByAssigneeMap(String processType, long processID, String assigneeFiled){
        HashMap<String,ArrayList<String>> assigneeMap=getAssigneeByProcess(processType,processID);
        ArrayList<String> AssigneeUserName=assigneeMap.get(assigneeFiled);
        return AssigneeUserName;
    }
    public HashMap<String,ArrayList<String>> getAssigneeByProcess(String processType, long processID){
        HashMap<String,ArrayList<String>> assigneeMap=new HashMap<String, ArrayList<String>>();
        MissivePublishFunction mpf=new MissivePublishFunction();
        if(processType.equals("missivePublish")){
            MissivePublish missivePublish=mpDAO.findByProcessID(processID);//------------->missivePublish form get
            MissivePublishForm missivePublishForm=mpf.MissivePublishToMissivePublishForm(missivePublish);

            //ArrayList<String> assignee =new ArrayList<String>(); db
            if(missivePublishForm.getDep_LeaderCheckUser()!=null){
                ArrayList<String> assignee =new ArrayList<String>();
                assignee.add(missivePublishForm.getDep_LeaderCheckUser().userName);//db
                assigneeMap.put("dep_LeaderCheck",assignee);
            }
            if(missivePublishForm.getDrafterUser()!=null){
                ArrayList<String> assignee =new ArrayList<String>();  //db
                //assignee.clear();  db
                assignee.add(missivePublishForm.getDrafterUser().userName);
                assigneeMap.put("drafterUser",assignee);
            }
            ArrayList<String> multiAssignee=new ArrayList<String>();
            if(missivePublishForm.getCounterSignUsers()!=null){
                for(int i=0;i<missivePublishForm.getCounterSignUsers().size();i++){
                    multiAssignee.add(missivePublishForm.getCounterSignUsers().get(i).userName);
                }
            }
            assigneeMap.put("counterSign_Person",multiAssignee);

            if(missivePublishForm.getOfficeCheckUser()!=null){
                //assignee.clear();  db
                ArrayList<String> assignee =new ArrayList<String>();
                assignee.add(missivePublishForm.getOfficeCheckUser().userName);
                assigneeMap.put("officeCheck",assignee);
            }
            if(missivePublishForm.getSignIssueUser()!=null){
                //assignee.clear(); db
                ArrayList<String> assignee =new ArrayList<String>();//db
                assignee.add(missivePublishForm.getSignIssueUser().userName);
                assigneeMap.put("signIssue_Person",assignee);
            }
            if(missivePublishForm.getPrinter()!=null){
                //assignee.clear(); db
                ArrayList<String> assignee =new ArrayList<String>();//db
                assignee.add(missivePublishForm.getPrinter().userName);
                assigneeMap.put("Printer",assignee);
            }
            if(missivePublishForm.getComposeUser()!=null){
                //assignee.clear();   db
                ArrayList<String> assignee =new ArrayList<String>();  //db
                assignee.add(missivePublishForm.getComposeUser().userName);
                assigneeMap.put("composeUser",assignee);
            }
            if(missivePublishForm.getCheckReader()!=null){
                // assignee.clear();   db
                ArrayList<String> assignee =new ArrayList<String>(); //db
                assignee.add(missivePublishForm.getCheckReader().userName);
                assigneeMap.put("CheckReader",assignee);
            }

        }

         return assigneeMap;
    }


 /*   for (int i=0;i<attachs.length;i++) {
        String titleWithPath = attachs[i];//getAttachmentTittle();
        String attachSuffix=titleWithPath.substring(titleWithPath.lastIndexOf(".")+1,titleWithPath.length());
        //保存由附件生成的pdf
        if(suffixArr.contains(attachSuffix)) {
            String attachFile = titleWithPath.substring(0, titleWithPath.lastIndexOf("."));
            String attachFileName=titleWithPath.substring(titleWithPath.lastIndexOf("/")+1,titleWithPath.lastIndexOf("."));

            //String attachFile = fileBasePath + title;
            String attachPdf = attachFile + ".pdf";
            String attachpdfName = attachFileName+".pdf";
            //防止重复转换
            //if(!FileOperate.exitFile(attachPdf)) {
            ConvertPdf(titleWithPath, attachPdf, convertTool);
            //}

            //pdf信息存入公文附件中，防止重复上传附件信息
            ArrayList<String> attFiles = actR.getAttachmentByVersionId(Long.parseLong(LastVersionID));

            if(!attFiles.contains(attachpdfName)||attFiles==null) {
                if(FileOperate.exitFolder(attachPdf)) {//判断是否存在对应的转换pdf
                    Attachment actPdf = new Attachment();
                    actPdf.setOriginalFile(false);
                    actPdf.setAttachmentTittle(attachpdfName);
                    actPdf.setAttachmentFilePath(attachPdf);
                    actPdf.setMissiveVersion(mVersion);
                    actR.save(actPdf);
                    //actl.add(actPdf);
                    pdf4Merge.add(attachPdf);
                }
            }




        }
        else if(attachSuffix.equals("pdf")||attachSuffix.equals("PDF")){
            pdf4Merge.add(attachs[i]);
        }
    }
}
else{
        for(String attPdf:attachs){
        String attachSuffix=attPdf.substring(attPdf.lastIndexOf(".")+1,attPdf.length());
        if(attachSuffix.equals("pdf")||attachSuffix.equals("PDF")){
        pdf4Merge.add(attPdf);
        }
        }
        }*/



}
