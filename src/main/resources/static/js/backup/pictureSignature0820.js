/**
 * Created by seky on 14/12/15.
 */



/*
 使用前确认导入必要的文件
 1.kendoui.js
 2.jSignature.js
 3.ace.css



 copyright---AlvinWei---shou
 */

var myStage;
var flag = false;
var hw_connectFlag = false;
var socketID;
var handwritingBttn;
var handwritingImg;
var signElementHeight;
var signElementWidth;

function openPictureSignatureWindow(option) {
    var signElement = option.sElement;
    signElementWidth = option.sWidth;
    signElementHeight = option.sHeight;
    var imgElement = option.imgElement;
    var imgElementWidth = option.imgWidth;
    var imgElementHeight = option.imgHeight;
    var previousSign = option.previousSign;
    var signature=option.signature;
    var textareaElement = option.textElement;
    var userID = option.userID;
    socketID = option.socketID;
//    socketID="123";//用于对话的标识

    var imgSrcValue = $("#" + imgElement).attr("src");
    var window_width = $(window).width();
    if (($(window).height() - signElementHeight - 120) / 2 > 0) {
        $("#" + signElement).css('top', $(document).scrollTop() + ($(window).height() - signElementHeight - 120) / 2);
        //wtop=($(window).height()-310)/2+this.window.screenTop;
    }
    else {
        $("#" + signElement).css('top', "100px");
        //wtop=100+this.window.screenTop;
    }
    var SWindow = document.createElement("div");
    SWindow.id = "psWindow";
    $(SWindow).appendTo($("#" + signElement));
    $("#psWindow").kendoWindow({
        close: function (e) {
            // close animation has finished playing
//            console.log(e)

            pswindow = $("#psWindow").data("kendoWindow");
            disconnect();
            pswindow.destroy();
            pswindow.wrapper.remove();
        },
        animation: {
            close: {
                duration: 500
            }
        }, width: signElementWidth, height: signElementHeight + 45, modal: true, position: {
            left: window_width / 2 - signElementWidth / 2
        }, resizable: false

    });
    var signatureTextdiv = document.createElement("textarea");
    $(signatureTextdiv).css({"position": "absolute", "left": "0px", "margin": "0px", "padding": "0px", "border": "none"});
    var text_fontSize = 18;
    var text_fontfamily = "SimSun";
    try {
//        console.log(textareaElement);
        textvalue = $("#" + textareaElement).val();
        $(signatureTextdiv).val($("#" + textareaElement).val());
        var temp_fontsize = $("#" + textareaElement).css("font-size");
        text_fontSize = temp_fontsize.substring(0, temp_fontsize.length - 2);
//        console.log(text_fontSize);
        text_fontfamily = $("#" + textareaElement).css("font-family");
    }
    catch (e) {
//        console.log(e);
    }

    $(signatureTextdiv).css({
        "width": signElementWidth + 'px',
        "height": signElementHeight + "px",
        "font-size": text_fontSize * signElementHeight / imgElementHeight + "px",
        "font-family": text_fontfamily
    })
    $(signatureTextdiv).appendTo($(SWindow));


    var signaturePlace = document.createElement("div");
    $(signaturePlace).css({
        "position": "absolute",
        "left": "0px",
        "background": "none"
    });
    $(signaturePlace).attr("id", "pictureSignatureContainer");
    $(signaturePlace).appendTo($(SWindow));
    flag = false;
    myStage = initStage('pictureSignatureContainer', signElementWidth, signElementHeight);
    if (imgSrcValue != "" && imgSrcValue != undefined) {
        loadBackgroundImages(imgSrcValue, flag, myStage, {
            x: 0,
            y: 0,
            width: signElementWidth,
            height: signElementHeight
        });
    }

    var btn_contain = document.createElement("div");
    $(btn_contain).css("position", "absolute");
    var excitBttn = document.createElement("a");
    excitBttn.className = "btn btn-xs btn-danger";
//    $(excitBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(excitBttn).css({
        "width": "100px"
    });
    var excitImg = document.createElement("i");
    excitImg.className = "icon-remove bigger-110";
    $(excitImg).appendTo($(excitBttn));
    var excitspan = document.createElement("span");
    excitspan.textContent = "取消签名";
    $(excitspan).appendTo($(excitBttn));
    excitBttn.onclick = function () {
        var dialog = $(SWindow).data("kendoWindow");
        disconnect();
        dialog.close();
    };
    $(excitBttn).appendTo($(btn_contain));


    var signatureBttn = document.createElement("a");
//    $(signatureBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(signatureBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    signatureBttn.className = "btn btn-xs";
    var signatureImg = document.createElement("i");
    signatureImg.className = "icon-pencil bigger-110";
    $(signatureImg).appendTo($(signatureBttn));
    var signatureSpan = document.createElement("span");
    signatureSpan.textContent = " 插入签名";
    $(signatureSpan).appendTo($(signatureBttn));
    signatureBttn.onclick = function () {
        if(signature!=null){
            addimage(userID);
        }
        else{
            alert("您还没有上传过您的签名！");
        }
    };
    $(signatureBttn).appendTo($(btn_contain));
    handwritingBttn = document.createElement("a");

//    $(handwritingBttn).attr("href", "/handSignaturePadOpen/" + socketID);
//    $(handwritingBttn)[0].style.width = signElementWidth / 4 - 20 + "px";
    $(handwritingBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    handwritingBttn.className = "btn btn-xs btn-light";
    handwritingImg = document.createElement("i");
    handwritingImg.className = "fa fa-toggle-off fa-lg";
    $(handwritingImg).appendTo($(handwritingBttn));
//    var handwritingImgS = document.createElement("span");
//    $(handwritingImgS).css("margin-top","-9px");
//    $(handwritingImgS).css("height","24px");
//    handwritingImgS.className = "fa-stack fa-lg";
//    $(handwritingImgS).html('<i class="fa fa-wifi fa-stack-1x"></i><i class="fa fa-ban fa-stack-1x text-danger"></i>');
//    $(handwritingImgS).appendTo($(handwritingBttn));

    var handwritingSpan = document.createElement("span");
    handwritingSpan.textContent = " 手写板";
    $(handwritingSpan).appendTo($(handwritingBttn));

    handwritingBttn.onclick = function () {
        if (!hw_connectFlag) {
            connect();
        }
        else {
            disconnect();

        }
    };
    $(handwritingBttn).appendTo($(btn_contain));
    handwritingBttn.click();

    var cancelBttn = document.createElement("a");
    cancelBttn.className = "btn btn-xs btn-warning";
//    $(excitBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(cancelBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    var cancelImg = document.createElement("i");
    cancelImg.className = "icon-undo bigger-110";
    $(cancelImg).appendTo($(cancelBttn));
    var cancelspan = document.createElement("span");
    cancelspan.textContent = "撤销重签";
    $(cancelspan).appendTo($(cancelBttn));
    cancelBttn.onclick = function () {
//qwer
//        delCircle();//删除4个点
//        console.log("previousSign",previousSign);
//        myStage.draw()
//        console.log("myStage.draw()",myStage)
        myStage = initStage('pictureSignatureContainer', signElementWidth, signElementHeight);
        if (previousSign != "" && previousSign != undefined) {
            loadBackgroundImages(previousSign, flag, myStage, {
                x: 0,
                y: 0,
                width: signElementWidth,
                height: signElementHeight
            });
        }


    };
    $(cancelBttn).appendTo($(btn_contain));



    var submitBttn = document.createElement("a");
//    $(submitBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(submitBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    submitBttn.className = "btn btn-xs btn-primary";
    var submitImg = document.createElement("i");
    submitImg.className = "icon-ok bigger-110";
    $(submitImg).appendTo($(submitBttn));
    var submitspan = document.createElement("span");
    submitspan.textContent = " 确认";
    $(submitspan).appendTo($(submitBttn));
    submitBttn.onclick = function () { // Note this is a function
        var sigdiv = $(signaturePlace);
        delCircle();//删除4个点
        myStage.toDataURL({
            callback: function (dataUrl) {
                $("#" + imgElement).attr("src", dataUrl);
//                console.log("base64", dataUrl);
            }
        });

        //todo 保存图片
        disconnect();
        var dialog = $(SWindow).data("kendoWindow");
        dialog.close();
    };

    $(submitBttn).appendTo($(btn_contain));
    $(btn_contain).css({"left": (signElementWidth - ($(btn_contain).width)) / 2 + "px", "top": signElementHeight + 14 + "px"})
    $(btn_contain).appendTo($(SWindow));

}
function openPictureSignatureWindow_missiveRecieve_leader(option) {
    //为了迅速完成领导奇奇怪怪的要求，下面代码就是一坨屎，大神勿喷
    var signElement = option.sElement;
    signElementWidth = option.sWidth;
    //signElementWidth=790;
    signElementHeight = option.sHeight;
    //signElementHeight=603;
    var imgElement = option.imgElement;
    var imgElementWidth = option.imgWidth;
    var imgElementHeight = option.imgHeight;

    var previousSign = option.previousSign;
    var signature=option.signature;
    var textareaElement = $("#"+option.textElement).css("width")==undefined?option.textElement+"s":option.textElement;

    var userID = option.userID;
    socketID = option.socketID;
//    socketID="123";//用于对话的标识
    var height_scale=signElementHeight/imgElementHeight;
    var imgSrcValue = $("#" + imgElement).attr("src");
    var window_width = $(window).width();
    if (($(window).height() - signElementHeight - 120) / 2 > 0) {
        $("#" + signElement).css('top', $(document).scrollTop() + ($(window).height() - signElementHeight - 120) / 2);
        //wtop=($(window).height()-310)/2+this.window.screenTop;
    }
    else {
        $("#" + signElement).css('top', "100px");
        //wtop=100+this.window.screenTop;
    }
    var SWindow = document.createElement("div");
    SWindow.id = "psWindow";
    $(SWindow).appendTo($("#" + signElement));
    $("#psWindow").kendoWindow({
        close: function (e) {
            // close animation has finished playing
//            console.log(e)

            pswindow = $("#psWindow").data("kendoWindow");
            disconnect();
            pswindow.destroy();
            pswindow.wrapper.remove();
        },
        animation: {
            close: {
                duration: 500
            }
        }, width: signElementWidth, height: signElementHeight + 45, modal: true, position: {
            left: window_width / 2 - signElementWidth / 2
        }, resizable: false

    });

    //background for leader
    var bg_base = document.createElement("div");
    bg_base.height=signElementHeight;
    bg_base.width=signElementWidth;
    $(bg_base).css(
        {
            "position": "absolute",
            "left": "0px",
            "margin": "0px",
            "padding": "0px",
            "border": "none",
            "width":signElementWidth,
            "height":signElementHeight,
            "background-image":"url(/img/missiveReceive_sign_bg.png)",
            "background-repeat":"no-repeat"
            ,"background-size":810*height_scale+"px "+603*height_scale+"px"
            ,"background-position-x":(-8)*height_scale+"px"

        }
    );// X-414px
    $(bg_base).appendTo($(SWindow));
    //lable from office
    var office_lable = document.createElement("lable");
    try{
        $(office_lable).html($("#officeSuggests").html());
        $(office_lable).css(
            {
                "position": "absolute",
                "left": "0px",
                "top": 36*height_scale+"px",
                "margin": "0px",
                "padding": "0px",
                "font-size":($("#officeSuggests").css("font-size")).substring(0, ($("#officeSuggests").css("font-size")).length - 2)*height_scale+"px",
                "height":($("#officeSuggests").css("height")).substring(0, ($("#officeSuggests").css("height")).length - 2)*height_scale+"px",
                "width":($("#officeSuggests").css("width")).substring(0, ($("#officeSuggests").css("width")).length - 2)*height_scale+"px"
            }
        );
    }
    catch (e){}
    $(office_lable).appendTo($(SWindow));

    var signatureTextdiv = document.createElement("textarea");
    $(signatureTextdiv).css({"position": "absolute", "left": "0px", "margin": "0px", "padding": "0px", "border": "none"});
    var text_div_top = 0;
    try {
        $(signatureTextdiv).val($("#" + textareaElement).val());
        var temp_top = $("#" + textareaElement).parent().css("top");
        text_div_top = temp_top.substring(0, temp_top.length - 2);
    }
    catch (e) {
    }

    $(signatureTextdiv).css({
        "top":(text_div_top -414 )*height_scale+"px",
        "width": ($("#" + textareaElement).css("width")).substring(0, ($("#" + textareaElement).css("width")).length - 2)*height_scale+"px",
        "height":($("#" + textareaElement).css("height")).substring(0, ($("#" + textareaElement).css("height")).length - 2)*height_scale+"px",
        "font-size": ($("#" + textareaElement).css("font-size")).substring(0, ($("#" + textareaElement).css("font-size")).length - 2)*height_scale+"px",
        "font-family":  $("#" + textareaElement).css("font-family"),
        "background":"none"
    })
    $(signatureTextdiv).appendTo($(SWindow));

    //lable from 阅办
    var yueban_div=$("#lookSignAreas");
    var yueban_lable = document.createElement("lable");
    try{
        $(yueban_lable).html(yueban_div.html());
        $(yueban_lable).css(
            {
                "position": "absolute",
                "left": "0px",
                "top": 36*height_scale +"px",
                "margin": "0px",
                "padding": "0px",
                "font-size":(yueban_div.css("font-size")).substring(0, (yueban_div.css("font-size")).length - 2)*height_scale+"px",
                "height":(yueban_div.css("height")).substring(0, (yueban_div.css("height")).length - 2)*height_scale+"px",
                "width":(yueban_div.css("width")).substring(0, (yueban_div.css("width")).length - 2)*height_scale+"px"
            }
        );
    }
    catch (e){}
    $(yueban_lable).appendTo($(SWindow));


    //img from 阅办
    var yueban_img=$("#lookSign_img");
    var temp_yueban_img = document.createElement("img");
    try{
        $(temp_yueban_img).attr("src",yueban_img.attr("src"));
        var temp_top = yueban_img.parent().css("top");
        var img_div_top = temp_top.substring(0, temp_top.length - 2);
        $(temp_yueban_img).css(
            {
                "position": "absolute",
                "left": "0px",
                "top": (img_div_top-414)*height_scale+"px",
                "margin": "0px",
                "padding": "0px",
                "height":(yueban_img.parent().css("height")).substring(0, (yueban_img.parent().css("height")).length - 2)*height_scale+"px",
                "width":(yueban_img.parent().css("width")).substring(0, (yueban_img.parent().css("width")).length - 2)*height_scale+"px"
            }
        );
    }
    catch (e){}
    $(temp_yueban_img).appendTo($(SWindow));



    var signaturePlace = document.createElement("div");
    $(signaturePlace).css({
        "position": "absolute",
        "left": "0px",
        "background": "none"
    });
    $(signaturePlace).attr("id", "pictureSignatureContainer");
    $(signaturePlace).appendTo($(SWindow));
    flag = false;
    myStage = initStage('pictureSignatureContainer', signElementWidth, signElementHeight);
    if (imgSrcValue != "" && imgSrcValue != undefined) {
        loadBackgroundImages(imgSrcValue, flag, myStage, {
            x: 0,
            y: 0,
            width: signElementWidth,
            height: signElementHeight
        });
    }

    var btn_contain = document.createElement("div");
    $(btn_contain).css("position", "absolute");
    var excitBttn = document.createElement("a");
    excitBttn.className = "btn btn-xs btn-danger";
//    $(excitBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(excitBttn).css({
        "width": "100px"
    });
    var excitImg = document.createElement("i");
    excitImg.className = "icon-remove bigger-110";
    $(excitImg).appendTo($(excitBttn));
    var excitspan = document.createElement("span");
    excitspan.textContent = "取消签名";
    $(excitspan).appendTo($(excitBttn));
    excitBttn.onclick = function () {
        var dialog = $(SWindow).data("kendoWindow");
        disconnect();
        dialog.close();
    };
    $(excitBttn).appendTo($(btn_contain));


    var signatureBttn = document.createElement("a");
//    $(signatureBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(signatureBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    signatureBttn.className = "btn btn-xs";
    var signatureImg = document.createElement("i");
    signatureImg.className = "icon-pencil bigger-110";
    $(signatureImg).appendTo($(signatureBttn));
    var signatureSpan = document.createElement("span");
    signatureSpan.textContent = " 插入签名";
    $(signatureSpan).appendTo($(signatureBttn));
    signatureBttn.onclick = function () {
        if(signature!=null){
            addimage(userID);
        }
        else{
            alert("您还没有上传过您的签名！");
        }
    };
    $(signatureBttn).appendTo($(btn_contain));
    handwritingBttn = document.createElement("a");

//    $(handwritingBttn).attr("href", "/handSignaturePadOpen/" + socketID);
//    $(handwritingBttn)[0].style.width = signElementWidth / 4 - 20 + "px";
    $(handwritingBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    handwritingBttn.className = "btn btn-xs btn-light";
    handwritingImg = document.createElement("i");
    handwritingImg.className = "fa fa-toggle-off fa-lg";
    $(handwritingImg).appendTo($(handwritingBttn));
//    var handwritingImgS = document.createElement("span");
//    $(handwritingImgS).css("margin-top","-9px");
//    $(handwritingImgS).css("height","24px");
//    handwritingImgS.className = "fa-stack fa-lg";
//    $(handwritingImgS).html('<i class="fa fa-wifi fa-stack-1x"></i><i class="fa fa-ban fa-stack-1x text-danger"></i>');
//    $(handwritingImgS).appendTo($(handwritingBttn));

    var handwritingSpan = document.createElement("span");
    handwritingSpan.textContent = " 手写板";
    $(handwritingSpan).appendTo($(handwritingBttn));

    handwritingBttn.onclick = function () {
        if (!hw_connectFlag) {
            connect();
        }
        else {
            disconnect();

        }
    };
    $(handwritingBttn).appendTo($(btn_contain));
    handwritingBttn.click();

    var cancelBttn = document.createElement("a");
    cancelBttn.className = "btn btn-xs btn-warning";
//    $(excitBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(cancelBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    var cancelImg = document.createElement("i");
    cancelImg.className = "icon-undo bigger-110";
    $(cancelImg).appendTo($(cancelBttn));
    var cancelspan = document.createElement("span");
    cancelspan.textContent = "撤销重签";
    $(cancelspan).appendTo($(cancelBttn));
    cancelBttn.onclick = function () {
//qwer
//        delCircle();//删除4个点
//        console.log("previousSign",previousSign);
//        myStage.draw()
//        console.log("myStage.draw()",myStage)
        myStage = initStage('pictureSignatureContainer', signElementWidth, signElementHeight);
        if (previousSign != "" && previousSign != undefined) {
            loadBackgroundImages(previousSign, flag, myStage, {
                x: 0,
                y: 0,
                width: signElementWidth,
                height: signElementHeight
            });
        }


    };
    $(cancelBttn).appendTo($(btn_contain));



    var submitBttn = document.createElement("a");
//    $(submitBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(submitBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    submitBttn.className = "btn btn-xs btn-primary";
    var submitImg = document.createElement("i");
    submitImg.className = "icon-ok bigger-110";
    $(submitImg).appendTo($(submitBttn));
    var submitspan = document.createElement("span");
    submitspan.textContent = " 确认";
    $(submitspan).appendTo($(submitBttn));
    submitBttn.onclick = function () { // Note this is a function
        var sigdiv = $(signaturePlace);
        delCircle();//删除4个点
        myStage.toDataURL({
            callback: function (dataUrl) {
                $("#" + imgElement).attr("src", dataUrl);
//                console.log("base64", dataUrl);
            }
        });

        //todo 保存图片
        disconnect();
        var dialog = $(SWindow).data("kendoWindow");
        dialog.close();
    };

    $(submitBttn).appendTo($(btn_contain));
    $(btn_contain).css({"left": (signElementWidth - ($(btn_contain).width)) / 2 + "px", "top": signElementHeight + 14 + "px"})
    $(btn_contain).appendTo($(SWindow));

}
function openPictureSignatureWindow_missiveRecieve_yueban(option) {
    var signElement = option.sElement;
    signElementWidth = option.sWidth;
    signElementHeight = option.sHeight;
    var imgElement = option.imgElement;
    var imgElementWidth = option.imgWidth;
    var imgElementHeight = option.imgHeight;
    var previousSign = option.previousSign;
    var signature=option.signature;
    var textareaElement = option.textElement;
    var userID = option.userID;
    socketID = option.socketID;
//    socketID="123";//用于对话的标识
    var height_scale=signElementHeight/imgElementHeight;
    var imgSrcValue = $("#" + imgElement).attr("src");
    var window_width = $(window).width();
    if (($(window).height() - signElementHeight - 120) / 2 > 0) {
        $("#" + signElement).css('top', $(document).scrollTop() + ($(window).height() - signElementHeight - 120) / 2);
        //wtop=($(window).height()-310)/2+this.window.screenTop;
    }
    else {
        $("#" + signElement).css('top', "100px");
        //wtop=100+this.window.screenTop;
    }
    var SWindow = document.createElement("div");
    SWindow.id = "psWindow";
    $(SWindow).appendTo($("#" + signElement));
    $("#psWindow").kendoWindow({
        close: function (e) {
            // close animation has finished playing
//            console.log(e)

            pswindow = $("#psWindow").data("kendoWindow");
            disconnect();
            pswindow.destroy();
            pswindow.wrapper.remove();
        },
        animation: {
            close: {
                duration: 500
            }
        }, width: signElementWidth, height: signElementHeight + 45, modal: true, position: {
            left: window_width / 2 - signElementWidth / 2
        }, resizable: false

    });


    //img from leader


    var leader_img=$("#leaderInstuct_img");
    var yueban_img1=$("#lookSign_img");

    var temp_top = leader_img.parent().css("top");
    var img_div_top = temp_top.substring(0, temp_top.length - 2);

    var temp_top1 = yueban_img1.parent().css("top");
    var img_div_top1 = temp_top1.substring(0, temp_top1.length - 2);

    var temp_yueban_bg_div = document.createElement("div");

    $(temp_yueban_bg_div).css(
        {
            "position": "absolute",
            "left": "0px",
            "top": "0px",
            "margin": "0px",
            "padding": "0px",
            "height":signElementHeight+"px",
            "width":signElementWidth+"px",
            "background-image":"url("+leader_img.attr("src")+")",
            "background-repeat":"no-repeat"
            ,"background-size":height_scale*(leader_img.parent().css("width").substring(0, leader_img.parent().css("width").length - 2))+"px" +" "+height_scale*(leader_img.parent().css("height").substring(0, leader_img.parent().css("height").length - 2))+"px"
            ,"background-position-y":(img_div_top-img_div_top1)*height_scale +"px"
        }
    );

    $(temp_yueban_bg_div).appendTo($(SWindow));



    var temp_yueban_img = document.createElement("img");
    try{
        $(temp_yueban_img).attr("src",yueban_img1.attr("src"));
        var temp_top = yueban_img1.parent().css("top");
        var img_div_top = temp_top.substring(0, temp_top.length - 2);
        $(temp_yueban_img).css(
            {
                "position": "absolute",
                "left": "0px",
                "top": (img_div_top-414)*height_scale+"px",
                "margin": "0px",
                "padding": "0px",
                "height":yueban_img1.parent().css("height"),
                "width":yueban_img1.parent().css("width")
            }
        );
    }
    catch (e){}
    //$(temp_yueban_img).appendTo($(SWindow));

    var signatureTextdiv = document.createElement("textarea");
    $(signatureTextdiv).css({"position": "absolute", "left": "0px", "margin": "0px", "padding": "0px", "border": "none",background: "none"});
    var text_fontSize = 18;
    var text_fontfamily = "SimSun";
    try {
//        console.log(textareaElement);
        textvalue = $("#" + textareaElement).val();
        $(signatureTextdiv).val($("#" + textareaElement).val());
        var temp_fontsize = $("#" + textareaElement).css("font-size");
        text_fontSize = temp_fontsize.substring(0, temp_fontsize.length - 2);
//        console.log(text_fontSize);
        text_fontfamily = $("#" + textareaElement).css("font-family");
    }
    catch (e) {
//        console.log(e);
    }

    $(signatureTextdiv).css({
        "width": signElementWidth + 'px',
        "height": signElementHeight + "px",
        "font-size": text_fontSize * height_scale + "px",
        "font-family": text_fontfamily
    })
    $(signatureTextdiv).appendTo($(SWindow));


    var signaturePlace = document.createElement("div");
    $(signaturePlace).css({
        "position": "absolute",
        "left": "0px",
        "background": "none"
    });
    $(signaturePlace).attr("id", "pictureSignatureContainer");
    $(signaturePlace).appendTo($(SWindow));
    flag = false;
    myStage = initStage('pictureSignatureContainer', signElementWidth, signElementHeight);
    if (imgSrcValue != "" && imgSrcValue != undefined) {
        loadBackgroundImages(imgSrcValue, flag, myStage, {
            x: 0,
            y: 0,
            width: signElementWidth,
            height: signElementHeight
        });
    }

    var btn_contain = document.createElement("div");
    $(btn_contain).css("position", "absolute");
    var excitBttn = document.createElement("a");
    excitBttn.className = "btn btn-xs btn-danger";
//    $(excitBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(excitBttn).css({
        "width": "100px"
    });
    var excitImg = document.createElement("i");
    excitImg.className = "icon-remove bigger-110";
    $(excitImg).appendTo($(excitBttn));
    var excitspan = document.createElement("span");
    excitspan.textContent = "取消签名";
    $(excitspan).appendTo($(excitBttn));
    excitBttn.onclick = function () {
        var dialog = $(SWindow).data("kendoWindow");
        disconnect();
        dialog.close();
    };
    $(excitBttn).appendTo($(btn_contain));


    var signatureBttn = document.createElement("a");
//    $(signatureBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(signatureBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    signatureBttn.className = "btn btn-xs";
    var signatureImg = document.createElement("i");
    signatureImg.className = "icon-pencil bigger-110";
    $(signatureImg).appendTo($(signatureBttn));
    var signatureSpan = document.createElement("span");
    signatureSpan.textContent = " 插入签名";
    $(signatureSpan).appendTo($(signatureBttn));
    signatureBttn.onclick = function () {
        if(signature!=null){
            addimage(userID);
        }
        else{
            alert("您还没有上传过您的签名！");
        }
    };
    $(signatureBttn).appendTo($(btn_contain));
    handwritingBttn = document.createElement("a");

//    $(handwritingBttn).attr("href", "/handSignaturePadOpen/" + socketID);
//    $(handwritingBttn)[0].style.width = signElementWidth / 4 - 20 + "px";
    $(handwritingBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    handwritingBttn.className = "btn btn-xs btn-light";
    handwritingImg = document.createElement("i");
    handwritingImg.className = "fa fa-toggle-off fa-lg";
    $(handwritingImg).appendTo($(handwritingBttn));
//    var handwritingImgS = document.createElement("span");
//    $(handwritingImgS).css("margin-top","-9px");
//    $(handwritingImgS).css("height","24px");
//    handwritingImgS.className = "fa-stack fa-lg";
//    $(handwritingImgS).html('<i class="fa fa-wifi fa-stack-1x"></i><i class="fa fa-ban fa-stack-1x text-danger"></i>');
//    $(handwritingImgS).appendTo($(handwritingBttn));

    var handwritingSpan = document.createElement("span");
    handwritingSpan.textContent = " 手写板";
    $(handwritingSpan).appendTo($(handwritingBttn));

    handwritingBttn.onclick = function () {
        if (!hw_connectFlag) {
            connect();
        }
        else {
            disconnect();

        }
    };
    $(handwritingBttn).appendTo($(btn_contain));
    handwritingBttn.click();

    var cancelBttn = document.createElement("a");
    cancelBttn.className = "btn btn-xs btn-warning";
//    $(excitBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(cancelBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    var cancelImg = document.createElement("i");
    cancelImg.className = "icon-undo bigger-110";
    $(cancelImg).appendTo($(cancelBttn));
    var cancelspan = document.createElement("span");
    cancelspan.textContent = "撤销重签";
    $(cancelspan).appendTo($(cancelBttn));
    cancelBttn.onclick = function () {
//qwer
//        delCircle();//删除4个点
//        console.log("previousSign",previousSign);
//        myStage.draw()
//        console.log("myStage.draw()",myStage)
        myStage = initStage('pictureSignatureContainer', signElementWidth, signElementHeight);
        if (previousSign != "" && previousSign != undefined) {
            loadBackgroundImages(previousSign, flag, myStage, {
                x: 0,
                y: 0,
                width: signElementWidth,
                height: signElementHeight
            });
        }


    };
    $(cancelBttn).appendTo($(btn_contain));



    var submitBttn = document.createElement("a");
//    $(submitBttn).css("width", signElementWidth / 4 - 20 + "px");
    $(submitBttn).css({
        "width": "100px",
        "margin-left": "10px"
    });
    submitBttn.className = "btn btn-xs btn-primary";
    var submitImg = document.createElement("i");
    submitImg.className = "icon-ok bigger-110";
    $(submitImg).appendTo($(submitBttn));
    var submitspan = document.createElement("span");
    submitspan.textContent = " 确认";
    $(submitspan).appendTo($(submitBttn));
    submitBttn.onclick = function () { // Note this is a function
        var sigdiv = $(signaturePlace);
        delCircle();//删除4个点
        myStage.toDataURL({
            callback: function (dataUrl) {
                $("#" + imgElement).attr("src", dataUrl);
//                console.log("base64", dataUrl);
            }
        });

        //todo 保存图片
        disconnect();
        var dialog = $(SWindow).data("kendoWindow");
        dialog.close();
    };

    $(submitBttn).appendTo($(btn_contain));
    $(btn_contain).css({"left": (signElementWidth - ($(btn_contain).width)) / 2 + "px", "top": signElementHeight + 14 + "px"})
    $(btn_contain).appendTo($(SWindow));

}
function update(activeAnchor) {
    var group = activeAnchor.getParent();

    var topLeft = group.find('.topLeft')[0];
    var topRight = group.find('.topRight')[0];
    var bottomRight = group.find('.bottomRight')[0];
    var bottomLeft = group.find('.bottomLeft')[0];
    var removeBtn = group.find('.topRightRemove')[0];
    var dateText;
    try{
        dateText = group.find('.dateText')[0];
    }
    catch (e){}
    var image = group.find('.image')[0];

    var anchorX = activeAnchor.x();
    var anchorY = activeAnchor.y();

    // update anchor positions
    switch (activeAnchor.name()) {
        case 'topLeft':
            removeBtn.y(anchorY);
            bottomLeft.x(anchorX);
            break;
        case 'bottomRight':
            bottomLeft.y(anchorY);
            removeBtn.x(anchorX);
            try{
                dateText.x(anchorX);
                dateText.y(anchorY - 20);
            }
            catch (e){}
            break;
        case 'bottomLeft':
            bottomRight.y(anchorY);
            topLeft.x(anchorX);
            try{
                dateText.y(anchorY - 20);
            }
            catch (e){}
            break;

    }

    image.setPosition(topLeft.getPosition());

    var width = removeBtn.x() - topLeft.x();
    var height = bottomLeft.y() - topLeft.y();
    if (width && height) {
        image.setSize({width: width, height: height});
    }
}


function addAnchor(group, x, y, name) {
    var stage = group.getStage();
    var layer = group.getLayer();

    var anchor = new Kinetic.Circle({
        x: x,
        y: y,
        stroke: '#666',
        fill: '#ddd',
        strokeWidth: 2,
        radius: 8,
        name: name,
        draggable: true,
        dragOnTop: false
    });

    if (name.indexOf("Remove") > -1) {
        anchor = new Kinetic.Circle({
            x: x,
            y: y,
            stroke: '#666',
            fill: '#f00',
            strokeWidth: 2,
            radius: 8,
            name: name,
            draggable: true,
            dragOnTop: false
        });
    }

    anchor.on('dragmove', function () {
        update(this);
        layer.draw();
    });
    anchor.on('mousedown touchstart', function () {
        group.setDraggable(false);
        this.moveToTop();
    });
    anchor.on('dragend', function () {
        group.setDraggable(true);
        layer.draw();
    });
    // add hover styling
    anchor.on('mouseover', function () {
        var layer = this.getLayer();
        document.body.style.cursor = 'pointer';
        this.setStrokeWidth(4);
        layer.draw();
    });
    anchor.on('mouseout', function () {
//        console.log(this.name())
        var layer = this.getLayer();
        document.body.style.cursor = 'default';
        this.strokeWidth(2);
        layer.draw();
    });
    anchor.on('mouseup', function (e) {
//        console.log("mouseup", this)
//        console.log(this.name())
        var layer = this.getLayer();
        if (this.name().indexOf("Remove") > -1) {
//            console.log("btn remove")
            this.getParent().hide();
            layer.draw();
        }
        //this.moveToTop();
    });

    group.add(anchor);
}


function loadImages(source, stage, options) {

    var image = new Image();
    image.onload = function () {
        loadImagesCallBack(image, stage, options);
    }
    image.src = source;
}


function loadBackgroundImages(source, flag, stage, options) {

    var image = new Image();
    image.onload = function () {
        loadBackgroundImagesCallBack(image, flag, stage, options);
    }
    image.src = source;
}

function initStage(containerName, width, height) {
    var stage = new Kinetic.Stage({
        container: containerName,
        width: width,
        height: height
    });
    return stage;
}

function loadBackgroundImagesCallBack(image, flag, stage, options) {

//    console.log("loadBackgroundImagesCallBack", image);
    var backgroundGroup;
    var backgroundImg;
    var layer = new Kinetic.Layer();
    if (!flag) {
        backgroundGroup = new Kinetic.Group({
            x: options.x,
            y: options.y,
            draggable: false
        });
        layer.add(backgroundGroup);
        //layer.add(yodaGroup);
        stage.add(layer);

        // darth vader
        backgroundImg = new Kinetic.Image({
            x: 0,
            y: 0,
            image: image,
            width: options.width,
            height: options.height,
            name: 'image'
        });
        backgroundGroup.add(backgroundImg);
        stage.draw();
    } else {
        backgroundGroup = new Kinetic.Group({
            x: options.x,
            y: options.y,
            draggable: true
        });
        var layer = new Kinetic.Layer();
        layer.add(backgroundGroup);
        //layer.add(yodaGroup);
        stage.add(layer);

        // darth vader
        backgroundImg = new Kinetic.Image({
            x: 0,
            y: 0,
            image: image,
            width: options.width,
            height: options.height,
            name: 'image'
        });
        backgroundGroup.add(backgroundImg);
        addAnchor(backgroundGroup, 0, 0, 'topLeft');
        //addAnchor(darthVaderGroup, 200, 0, 'topRight');
        addAnchor(backgroundGroup, options.width, 0, 'topRightRemove');
        addAnchor(backgroundGroup, options.width, options.height, 'bottomRight');
        //addAnchor(backgroundGroup, 0, options.height, 'bottomLeft');

        backgroundGroup.on('dragstart', function () {
            this.moveToTop();
        });
        stage.draw();
    }

}

function loadImagesCallBack(image, stage, options) {
    // var stage = new Kinetic.Stage({
    //   container: 'container',
    //   width: options.width,
    //   height: options.height
    // });
//    console.log("loadImagesCallBack", image)
    var signatureGroup = new Kinetic.Group({
        x: options.x,
        y: options.y,
        draggable: true
    });

    var layer = new Kinetic.Layer();

    /*
     * go ahead and add the groups
     * to the layer and the layer to the
     * stage so that the groups have knowledge
     * of its layer and stage
     */
    layer.add(signatureGroup);
    //layer.add(yodaGroup);
    stage.add(layer);

    // darth vader
    var signatureImg = new Kinetic.Image({
        x: 0,
        y: 0,
        image: image,
        width: options.width,
        height: options.height,
        name: 'image'
    });
    signatureGroup.add(signatureImg);
    var d = new Date();
    if(options.needDate){0
        var simpleText = new Kinetic.Text({
            x: options.width,
            y: options.height - 20,
            text: d.toLocaleDateString(),
            fontSize: 20,
            name: "dateText",
            fontFamily: 'hei',
            fill: 'black'
        });
        signatureGroup.add(simpleText);
    }

    addAnchor(signatureGroup, 0, 0, 'topLeft');
    //addAnchor(darthVaderGroup, 200, 0, 'topRight');
    addAnchor(signatureGroup, options.width, 0, 'topRightRemove');
    addAnchor(signatureGroup, options.width, options.height, 'bottomRight');
    addAnchor(signatureGroup, 0, options.height, 'bottomLeft');

    signatureGroup.on('dragstart', function () {
        this.moveToTop();
    });


    stage.draw();
}
function addimage(userID) {
//    console.log("addimage")
    loadImages("/user/getSignImg/" + userID, myStage, {
        x: 100,
        y: 100,
        width: 200,
        height: 100,
        needDate:true
    });
}
function delCircle() {

    var shapes = myStage.get('.topLeft')
    for (i = 0; i < shapes.length; i++) {
//        console.log(shapes[i].getId());
        shapes[i].hide()
    }
    var shapes = myStage.get('.topRight')
    for (i = 0; i < shapes.length; i++) {
//        console.log(shapes[i].getId());
        shapes[i].hide()
    }
    var shapes = myStage.get('.bottomRight')
    for (i = 0; i < shapes.length; i++) {
//        console.log(shapes[i].getId());
        shapes[i].hide()
    }
    var shapes = myStage.get('.bottomLeft')
    for (i = 0; i < shapes.length; i++) {
//        console.log(shapes[i].getId());
        shapes[i].hide()
    }
    var shapes = myStage.get('.topRightRemove')
    for (i = 0; i < shapes.length; i++) {
//        console.log(shapes[i].getId());
        shapes[i].hide()
    }
    myStage.draw()


}

var stompClient = null;
function connect() {
    //window.location.href = "/handSignaturePadOpen/" + socketID;
    var tipsWindow;
    var wtop=(window.screen.availHeight-310)/2;
    var wleft=(window.screen.availWidth-450)/2;
    //tipsWindow=window.open("/handSignaturePadOpen/" + socketID ,"提示","width=450,height=310,top="+wtop+",left="+wleft) ;
    tipsWindow=window.open("/handSignaturePadOpen/" + socketID+"/"+signElementWidth+"/"+signElementHeight ,"提示","width=450,height=310,top="+wtop+",left="+wleft) ;
    console.log(tipsWindow)

    setTimeout(function(){
        {
            if(tipsWindow.document.body.innerHTML==""){
                tipsWindow.document.body.innerHTML='<div style="margin: 5px"><h3>手写板不可用！请检查手写板是否连接正常！</h3></div>';
                //alert("手写板不可用！请检查手写板是否连接正常！");
                tipsWindow.close();//调用关闭方法
                tipsWindow=null//并把值赋成null
                //setTimeout(function()
                //    {
                //
                //    },5000);

            }
            //console.log("1222",tipsWindow.document.body.innerHTML)
        }
    },1000);
    //setTimeout(function(){
    //    {//关闭打开的新窗口，否则提示
    //        if(tipsWindow)//如果Wind对象存在
    //        {
    //            tipsWindow.close();//调用关闭方法
    //
    //            tipsWindow=null//并把值赋成null
    //        }
    //
    //    }
    //},17000);

    var socket = new SockJS('/signatureEvent');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        $(handwritingBttn).attr("disabled",true);
//        $(handwritingBttn).removeAttr("href");
        handwritingImg.className = "fa fa-toggle-on fa-lg";
        hw_connectFlag=true;
//        setConnected(true);
//        console.log('Connected: ' + frame);
//        stompClient.subscribe('/app/broadcastingEvent/'+socketID, function(greeting){
//            console.log(JSON.parse(greeting.body).eventType)
//            console.log(greeting)
//            eventHandler(greeting.body);
//        });
        stompClient.subscribe('/app/broadcast/'+socketID, function(greeting){
//            console.log(JSON.parse(greeting.body).eventType)
//            console.log(greeting)
            eventHandler(greeting.body);
        });
    });
}

function disconnect() {
    if(stompClient!=null){
        stompClient.disconnect();
    }
//    $(handwritingBttn).attr("href","/handSignaturePadOpen/" + socketID);
    $(handwritingBttn).removeAttr("disabled");
    handwritingImg.className = "fa fa-toggle-off fa-lg";
    hw_connectFlag=false;
//    setConnected(false);
//    console.log("Disconnected");
}

function eventHandler(event) {
    var event = JSON.parse(event)

    if (event.eventType == "save"){
        addPadImage(event);
//        setTimeout("disconnect()",1000);
        disconnect()
    }
    else if(event.eventType == "close"){
        disconnect()
    }
    else{

    }



}
function addPadImage(event)
{
    var i = new Image();
    i.src = event.imageBase64;

    loadImages(event.imageBase64, myStage, {
        //x: 100,
        x:0,
        y: 0,
        width: i.width,
        height: i.height,
        needDate:false
    });
//    var image = $("<img / >")
//    image.attr("src", event.imageBase64)
//    $("#response").append(image);

//    showMessage(event.eventType + " "+ event.message);
}