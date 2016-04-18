/**
 * Created by AlvinWei on 14-9-22.
 */
function createFileListElement(fileListDiv_id,file,attachment_id,attachmentparent_id,editableElementList,pdfViewerURL,fileURL,fileUploadUrl,fileRemoveURL){
    //fileListDiv_id--文件列表的id
    //file：文件{name;extension;size}
    //attachment_id:附件控件的id   //其必须有父级元素
    //attachmentparent_id:父级元素
    //editableElementList：当前可编辑控件集合的数组
    //pdfViewerURL:pdf浏览器URL
    //fileURL：文件下载的URL
    //fileUploadUrl  //'/missivePublish/upload/'+instanceID,
    //fileRemoveURL;// "/missivePublish/remove/"+instanceID;


//    var fileURL="/pdf/missivePublish/"+instanceID+"/"+filePath_version+"/"+file.name;
//    var pdfViewerURL="/pdfViewer/web/viewer.html?file=";


    var href_URL=getfile(pdfViewerURL,fileURL,file.name);
    var show_text=file.name;
    var show_type=getfileShowType(file.extension);
   // var show_type="下载";
    if($.inArray(attachment_id, editableElementList) > -1){
        createFileListDiv(fileListDiv_id,href_URL,show_text,show_type,attachment_id,attachmentparent_id,editableElementList,fileUploadUrl,fileRemoveURL);
    }
    else{
        if(file.extension==".pdf"||file.extension==".PDF"){
            createFileListDiv(fileListDiv_id,href_URL,show_text,show_type,attachment_id,attachmentparent_id,editableElementList,fileUploadUrl,fileRemoveURL);
        }
        else {
            //
        }
    }

}

function createFileListDiv(fileListDiv_id,href_URL,show_text,show_type,attachment_id,attachmentparent_id,editableElementList,fileUploadUrl,fileRemoveURL){


    var profile_div = document.createElement("div");
    profile_div.className = "profile-feed";
    var profile_activity_div = document.createElement("div");
    profile_activity_div.className = "profile-activity clearfix";
    var noclass_div = document.createElement("div");
    var icon_folder_open_i = document.createElement("i");
    icon_folder_open_i.className = "icon-folder-open bigger-110";
    $(icon_folder_open_i).appendTo($(noclass_div));

    var user_a = document.createElement("a");
    user_a.className = "user";
    $(user_a).attr("href",href_URL);//add href
    user_a.target="_blank";
    user_a.innerHTML=show_text;//add name
    $(user_a).appendTo($(noclass_div));
    $(noclass_div).appendTo($(profile_activity_div));

    var tools_div = document.createElement("div");
    tools_div.className = "tools action-buttons";
    var icon_pencil_a = document.createElement("a");
    if(show_type=="查看"){
        icon_pencil_a.className ="icon-search bigger-125";
    }
    else icon_pencil_a.className ="icon-cloud-download bigger-125";

    $(icon_pencil_a).attr("href",href_URL);//add href
    icon_pencil_a.target="_blank";
    icon_pencil_a.innerHTML= show_type;
    $(icon_pencil_a).appendTo($(tools_div));


    if($.inArray(attachment_id, editableElementList) > -1){
        var icon_pencil1_a = document.createElement("a");
        icon_pencil1_a.className = "icon-remove red bigger-125";
        $(icon_pencil1_a).attr("href","javascript:void(0)");
        $(icon_pencil1_a)[0].data=show_text;//add href
        var ajax_url = fileRemoveURL;// "/missivePublish/remove/"+instanceID;
        icon_pencil1_a.onclick=function(e){
            var fileNames= e.target.data;
            $.ajax({
                url: ajax_url,
                type: 'POST',
                data: {fileNames: fileNames},
                success: function (result) {
                    $(e.target).parent().parent().parent().remove();//remove old elemennts

                    resultFiles=resultFiles.filter(function(el){
                        return el.name!==fileNames;
                    });

                    $("#"+attachmentparent_id).empty();
                    var inputdiv=document.createElement("input");
                    inputdiv.type="file";
                    inputdiv.name="files";
                    inputdiv.id=attachment_id;
                    $("#"+attachmentparent_id).append(inputdiv);
                    $("#"+attachment_id).kendoUpload({
                        multiple: true,
                        async: {
                            saveUrl: fileUploadUrl,
                            autoUpload: false
                        },
                        localization: {
                            select: "选择文件....",
                            uploadSelectedFiles:"上传",
                            statusFailed: "失败",
                            headerStatusUploaded: " ",
                            headerStatusUploading: "上传中..."
                            ,cancel: "取消"
                            ,remove:"删除"
                            ,retry:"重试"


                        },
                        select: onSelect,
                        success: onSuccess,
                        files: resultFiles
                    });
                    //set init_files visible be false;
                    for(var resultFiles_i=0;resultFiles_i<resultFiles.length;resultFiles_i++){
                        var hide_result_file=$("span[class='k-filename'][title='"+resultFiles[resultFiles_i].name+"']");
                        for(var hide_result_file_i=0;hide_result_file_i<hide_result_file.length;hide_result_file_i++){
                            $(hide_result_file[hide_result_file_i]).parent()[0].setAttribute("class","hide");
                        }
                    }

                }
            });
        }
        icon_pencil1_a.innerHTML= "删除";
        $(icon_pencil1_a).appendTo($(tools_div));

    }
    else{
//            uploadattachments.disable();
    }
    $(tools_div).appendTo($(profile_activity_div));
    $(profile_activity_div).appendTo($(profile_div));
    $(profile_div).appendTo($("#"+fileListDiv_id));
}
function getfile(pdfViewerURL,fileURL,name){

    if(name.split(".")[1]=="pdf"||name.split(".")[1]=="PDF"){
        return pdfViewerURL+fileURL;
    }
    else return fileURL;
}
function getfileShowType(extension){
    if(extension==".pdf"||extension==".PDF"){
        return "查看";
    }
    else return "下载";
}