/**
 * Created by sqhe18 on 14-12-8.
 */
function cloneObject(obj){
    var o;
    switch(typeof obj){
        case 'undefined': break;
        case 'string'   : o = obj + '';break;
        case 'number'   : o = obj - 0;break;
        case 'boolean'  : o = obj;break;
        case 'object'   :
            if(obj === null){
                o = null;
            }else{
                if(obj instanceof Array){
                    o = [];
                    for(var i = 0, len = obj.length; i < len; i++){
                        o.push(cloneObject(obj[i]));
                    }
                }else{
                    o = {};
                    for(var k in obj){
                        o[k] = cloneObject(obj[k]);
                    }
                }
            }
            break;
        default:
            o = obj;break;
    }
    return o;
}

function compareObject(o1,o2){
    if(typeof o1 != typeof o2)return false;
    if(typeof o1 == 'object'){
        for(var o in o1){
            if(typeof o2[o] == 'undefined')return false;
            if(!compareObject(o1[o],o2[o]))return false;
        }
        return true;
    }else{
        return o1 === o2;
    }
}
function isArraryEqual(a1, a2) {
    var arrry_equal = false;

    if(a1 instanceof Array && a2 instanceof Array)
    {
        if(a1.length==a2.length){
            for (var i=0,iLen=a1.length; i<iLen; i++)
            {
                var inArray=false;
                for (var j=0,jLen=a2.length; j<jLen; j++)
                {
                    var json1=a1[i];
                    var json2=a2[j];
                    if (compareObject(json1,json2))
//                            if (a1[i]==a2[j])
                    {
                        inArray=true;
                        break;
//                            return false;
                    }
                }
                if(inArray==false){
                    return false;
                }
                arrry_equal=inArray;
            }
            return arrry_equal;

        }
        else{
            return false;
        }

    }
    else return false;

}
String.prototype.endWith=function(endStr){
    var d=this.length-endStr.length;
    return (d>=0&&this.lastIndexOf(endStr)==d)
}