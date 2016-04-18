package cn.edu.shou.missive.domain;



import cn.edu.shou.missive.service.MissivePublishRepository;
import cn.edu.shou.missive.web.FileOperate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by seky on 14-8-3.
 * 放置一些常用处理函数
 */

public class CommonFunction {

    @Autowired
    MissivePublishRepository mpr;


    //获取当前年月日
    public int[] getCurrentDateTime() {
        Date date = new Date(); // your date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int yearC = cal.get(Calendar.YEAR);
        int monthC = cal.get(Calendar.MONTH)+1;
        int dayC = cal.get(Calendar.DAY_OF_MONTH);
        int[] arr = new int[]{yearC, monthC, dayC};
        return arr;
    }

    //根据对象数据返回数据值
    public  String getStrByObject(Object[] obj){
        String str="";
        for (int i=0;i<obj.length;++i){
                str+=((Group) obj[i]).id+",";//获取对象值
        }
        //去除最后一个,
        str=str.substring(0,str.length()-1);

        return str;
    }
    //上传文件
    public String uploadFiles(String realPath,MultipartFile file){
        //首先判断文件夹是否存在 不存在创建
        FileOperate.newFolder(realPath);
        if (!file.isEmpty()) {

            String name = file.getOriginalFilename();
            String fileName=realPath+name;
            try {

                //byte[] bytes = file.getBytes();
                InputStream fileInputStream = file.getInputStream();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(fileName)));

                byte[] bytes = new byte[1024];

                int bytesWritten = 0;
                int byteCount = 0;
                while ((byteCount = fileInputStream.read(bytes)) != -1)
                {
                   // int bytesLength=bytes.length;

                    stream.write(bytes, 0, byteCount);
                    bytesWritten += byteCount;
//                    stream.flush();
                }
                fileInputStream.close();
                //outputStream.close();
               // stream.write(fileInputStream.read());
                //stream.write(bytes);
                stream.close();
                return "true";
            } catch (Exception e) {
                return "false";
            }
        } else {
            return "false";
        }
    }

    public String shellOption(String htmlURL,String pdfURL) {
        //ExecuteShellComand obj = new ExecuteShellComand();
        String htmlpath=htmlURL;
        String pdfpath=pdfURL;
        String command  = "wkhtmltopdf -T 0 -B 0 -L 0 -R 0";
        command+=" "+htmlpath+" "+pdfpath;
//        command="wkhtmltopdf -T 0 -B 0 -L 0 -R 0 http://localhost:8888/missiveSign/missiveSignToPDF/2522 /Users/sqhe18/Desktop/missive2.pdf";
        String output = this.executeCommand(command);
        return output;
    }
    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "OK";

    }

}
