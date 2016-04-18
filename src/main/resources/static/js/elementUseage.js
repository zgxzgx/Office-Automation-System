/**
 * Created by Administrator on 2014/7/15.
 */
function elementUseage(elementID){
    var element=$("#"+elementID);
    try{
        element[0].disabled=false;
    }
    catch (error){
        console.log(error);
    }

}
function elementInputUseage(elementID){
    var element=$("#"+elementID);
    // bb[0].readOnly=true;
    //$("#a123").readOnly=true;
    try{
        element[0].disabled=false;
        // element[0] .style.background="none";
        element[0] .style.border="solid";
        element[0] .style.borderWidth="1px";
    }
    catch (error){
        console.log(error);
    }

}
function elementDisabled(elementID){
    var element=$("#"+elementID);
    // bb[0].readOnly=true;
    //$("#a123").readOnly=true;
    try{
        element[0].disabled=true;
//    element[0].readOnly=true;
//    element[0] .style.background="none";
//    console.log(element);
//    element.style.backgroundColor="red"
//    element[0].style.border="none";
    }
    catch (error){
        console.log(error);
    }

}
function elementInputDisabled(elementID){
    var element=$("#"+elementID);
    try{
        element[0].disabled=true;
        element[0] .style.background="none";
        element[0].style.border="none";
    }
    catch (error){
        console.log(error);
    }

}
function elementHidden(elementID){
    var element=$("#"+elementID);
    try{
        element[0].style.visibility='hidden';
    }
    catch (error){
        console.log(error);
    }

}
function elementShow(elementID){
    var element=$("#"+elementID);
    try{
        element[0].style.visibility = 'visible';
    }
    catch (error){
        console.log(error);
    }

}

function allElementDisabled_for_PM(){
    elementInputDisabled("MissiveID");//收文号
    elementInputDisabled("secretLevel");//密级
    elementInputDisabled("missiveType");//公文类型


    elementDisabled("signIssue_Person");//签发人员
    elementDisabled("signIssue_Content");//签发内容textArea
    elementHidden("signIssue_SignBtn");//签发签字按钮



    elementDisabled("counterSign_Person");//会签人员
    elementDisabled("counterSign_Content");//会签内容textArea
    elementHidden("counterSign_SignBtn");//会签签字按钮

    elementDisabled("mainSend_Person");//主送人员
    elementDisabled("mainSend_Content");//主送内容textArea
    elementHidden("mainSend_SignBtn");//主送签字按钮

    elementDisabled("copyTo_Person");//抄送人员
    elementDisabled("copyTo_Content");//抄送内容textArea
    elementHidden("copyTo_SignBtn");//抄送签字按钮


    elementDisabled("printCount");//打印份数


    elementDisabled("initiativePublic");
    elementDisabled("applyForPublic");
    elementDisabled("forbiddenPublic");

    elementDisabled("missiveTittle");
    elementDisabled("appendTittle");
}