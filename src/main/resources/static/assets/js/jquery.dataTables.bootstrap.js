//http://datatables.net/plug-ins/pagination#bootstrap
$.extend( true, $.fn.dataTable.defaults, {
     "sDom": "<'row'<'col-sm-6'l><'col-sm-6'f>t>r<'row'<'col-sm-6'i><'col-sm-6'p>>",
   // "sDom": "<'row'<'col-sm-6'l><'col-sm-6'f>r>t<'row'<'col-sm-6'i><'col-sm-6'p>>",

	"sPaginationType": "bootstrap",
	"oLanguage": {
		"sLengthMenu": "显示 _MENU_ 记录"
	},
            "oPaginate": {
            "sPrevious": "上一页 ",
            "sNext":     " 下一页",
            "sLast":     " >>",
            "sFirst":    "<< "
        }

} );


/* API method to get paging information */
$.fn.dataTableExt.oApi.fnPagingInfo = function ( oSettings )
{
    return {
        "iStart":         oSettings._iDisplayStart,
        "iEnd":           oSettings.fnDisplayEnd(),
        "iLength":        oSettings._iDisplayLength,
        "iTotal":         oSettings.fnRecordsTotal(),
        "iFilteredTotal": oSettings.fnRecordsDisplay(),
        "iPage":          Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength ),
        "iTotalPages":    Math.ceil( oSettings.fnRecordsDisplay() / oSettings._iDisplayLength )
    };
}
 
///* Bootstrap style pagination control */
//$.extend( $.fn.dataTableExt.oPagination, {
//    "bootstrap": {
//        "fnInit": function( oSettings, nPaging, fnDraw ) {
//            var oLang = oSettings.oLanguage.oPaginate;
//            var fnClickHandler = function ( e ) {
//                e.preventDefault();
//                if ( oSettings.oApi._fnPageChange(oSettings, e.data.action) ) {
//                    fnDraw( oSettings );
//                }
//            };
//
//            $(nPaging).append(
//                '<ul class="pagination">'+
//                    '<li class="prev disabled"><a href="#"><i class="icon-double-angle-left"></i></a></li>'+
//                    '<li class="next disabled"><a href="#"><i class="icon-double-angle-right"></i></a></li>'+
//                '</ul>'
//            );
//            var els = $('a', nPaging);
//            $(els[0]).bind( 'click.DT', { action: "previous" }, fnClickHandler );
//            $(els[1]).bind( 'click.DT', { action: "next" }, fnClickHandler );
//        },
//
//        "fnUpdate": function ( oSettings, fnDraw ) {
//            var iListLength = 5;
//            var oPaging = oSettings.oInstance.fnPagingInfo();
//            var an = oSettings.aanFeatures.p;
//            var i, j, sClass, iStart, iEnd, iHalf=Math.floor(iListLength/2);
//
//            if ( oPaging.iTotalPages < iListLength) {
//                iStart = 1;
//                iEnd = oPaging.iTotalPages;
//            }
//            else if ( oPaging.iPage <= iHalf ) {
//                iStart = 1;
//                iEnd = iListLength;
//            } else if ( oPaging.iPage >= (oPaging.iTotalPages-iHalf) ) {
//                iStart = oPaging.iTotalPages - iListLength + 1;
//                iEnd = oPaging.iTotalPages;
//            } else {
//                iStart = oPaging.iPage - iHalf + 1;
//                iEnd = iStart + iListLength - 1;
//            }
//
//            for ( i=0, iLen=an.length ; i<iLen ; i++ ) {
//                // Remove the middle elements
//                $('li:gt(0)', an[i]).filter(':not(:last)').remove();
//
//                // Add the new list items and their event handlers
//                for ( j=iStart ; j<=iEnd ; j++ ) {
//                    sClass = (j==oPaging.iPage+1) ? 'class="active"' : '';
//                    $('<li '+sClass+'><a href="#">'+j+'</a></li>')
//                        .insertBefore( $('li:last', an[i])[0] )
//                        .bind('click', function (e) {
//                            e.preventDefault();
//                            oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
//                            fnDraw( oSettings );
//                        } );
//                }
//
//                // Add / remove disabled classes from the static elements
//                if ( oPaging.iPage === 0 ) {
//                    $('li:first', an[i]).addClass('disabled');
//                } else {
//                    $('li:first', an[i]).removeClass('disabled');
//                }
//
//                if ( oPaging.iPage === oPaging.iTotalPages-1 || oPaging.iTotalPages === 0 ) {
//                    $('li:last', an[i]).addClass('disabled');
//                } else {
//                    $('li:last', an[i]).removeClass('disabled');
//                }
//            }
//        }
//    }
//} );

//$.extend( true, $.fn.dataTable.defaults, {
//    "sDom": "<'row-fluid'<'span6'l><'span6'f>r>t<'row-fluid'<'span6'i><'span6'p>>",
//    "sPaginationType": "bootstrap",
//    "oLanguage": {
//        "sSearch": "快速搜索:",
//        "bAutoWidth": true,
//        "sLengthMenu": "每页显示 _MENU_ 条记录",
//        "sZeroRecords": "Nothing found - 没有记录",
//        "sInfo": "_START_ 到 _END_ 条,共 _TOTAL_ 条",
//        "sInfoEmpty": "显示0条记录",
//        "sInfoFiltered": "(从 _MAX_ 条中过滤)",
//        "sProcessing":"<div style=''><img src='../static/img/loader.gif'><span>加载中...</span></div>",
//        "oPaginate": {
//            "sPrevious": "上一页 ",
//            "sNext":     " 下一页",
//            "sLast":     " >>",
//            "sFirst":    "<< "
//        }
//    }
//} );


/* Default class modification */
$.extend( $.fn.dataTableExt.oStdClasses, {
    "sWrapper": "dataTables_wrapper form-inline"
} );


/* API method to get paging information */
$.fn.dataTableExt.oApi.fnPagingInfo = function ( oSettings )
{
    return {
        "iStart":         oSettings._iDisplayStart,
        "iEnd":           oSettings.fnDisplayEnd(),
        "iLength":        oSettings._iDisplayLength,
        "iTotal":         oSettings.fnRecordsTotal(),
        "iFilteredTotal": oSettings.fnRecordsDisplay(),
        "iPage":          Math.ceil( oSettings._iDisplayStart / oSettings._iDisplayLength ),
        "iTotalPages":    Math.ceil( oSettings.fnRecordsDisplay() / oSettings._iDisplayLength )
    };
};




/* Bootstrap style pagination control */
$.extend( $.fn.dataTableExt.oPagination, {
    "bootstrap": {
        "fnInit": function( oSettings, nPaging, fnDraw ) {
            var oLang = oSettings.oLanguage.oPaginate;
            var fnClickHandler = function ( e ) {
                e.preventDefault();
                if ( oSettings.oApi._fnPageChange(oSettings, e.data.action) ) {
                    fnDraw( oSettings );
                }
            };

            $(nPaging).addClass('pagination').append(
                    '<ul>'+
                    '<li class="first disabled" style=" float:left;list-style: none"><a href="#">'+oLang.sFirst+'</a></li>'+
                    '<li class="prev disabled" style=" float:left;list-style: none"><a href="#">&larr; '+oLang.sPrevious+'</a></li>'+
                    '<li class="next disabled" style=" float:left;list-style: none"><a href="#">'+oLang.sNext+' &rarr; </a></li>'+
                    '<li class="last disabled" style=" float:left;list-style: none"><a href="#">'+oLang.sLast+'</a></li>'+
//                    '<input type="text" style="width: 45px;border-width: 0px" value="跳转" disabled="disabled">'+
                    '<input type="text" style="width: 45px " id="redirect" class="redirect">'+
                    '</ul>'
            );

            //datatables分页跳转
            $(nPaging).find(".redirect").keyup(function(e){
                var ipage = parseInt($(this).val());
                var oPaging = oSettings.oInstance.fnPagingInfo();
                if(isNaN(ipage) || ipage<1){
                    ipage = 1;
                }else if(ipage>oPaging.iTotalPages){
                    ipage=oPaging.iTotalPages;
                }
                $(this).val(ipage);
                ipage--;
                oSettings._iDisplayStart = ipage * oPaging.iLength;
                fnDraw( oSettings );
            });

            var els = $('a', nPaging);
            $(els[0]).bind( 'click.DT', {
                action: "first"
            }, fnClickHandler );
            $(els[1]).bind( 'click.DT', {
                action: "previous"
            }, fnClickHandler );
            $(els[2]).bind( 'click.DT', {
                action: "next"
            }, fnClickHandler );
            $(els[3]).bind( 'click.DT', {
                action: "last"
            }, fnClickHandler );
        },

        "fnUpdate": function ( oSettings, fnDraw ) {
            var iListLength = 5;
            var oPaging = oSettings.oInstance.fnPagingInfo();
            var an = oSettings.aanFeatures.p;
            var i, ien, j, sClass, iStart, iEnd, iHalf=Math.floor(iListLength/2);

            if ( oPaging.iTotalPages < iListLength) {
                iStart = 1;
                iEnd = oPaging.iTotalPages;
            }
            else if ( oPaging.iPage <= iHalf ) {
                iStart = 1;
                iEnd = iListLength;
            } else if ( oPaging.iPage >= (oPaging.iTotalPages-iHalf) ) {
                iStart = oPaging.iTotalPages - iListLength + 1;
                iEnd = oPaging.iTotalPages;
            } else {
                iStart = oPaging.iPage - iHalf + 1;
                iEnd = iStart + iListLength - 1;
            }

            for ( i=0, ien=an.length ; i<ien ; i++ ) {
                // Remove the middle elements
                ($('li:gt(1)', an[i]).filter(':not(:last)')).filter(':not(:last)').remove();

                // Add the new list items and their event handlers
                for ( j=iStart ; j<=iEnd ; j++ ) {
                    sClass = (j==oPaging.iPage+1) ? '<li class="active" style=" float:left;list-style: none;color:red" ><a href="#"><font size="3px" color="red"><strong>&nbsp'+j+'&nbsp</strong></font></a></li>' : '<li class="active" style=" float:left;list-style: none;color:red" ><a href="#">&nbsp'+j+'&nbsp</a></li>';
                    $( sClass )
//                    $('<li '+sClass+' ><a href="#">&nbsp'+j+'&nbsp</a></li>')
                        .insertBefore( $('.next', an[i])[0] )
                        .bind('click', function (e) {
                            e.preventDefault();
                            oSettings._iDisplayStart = (parseInt($('a', this).text(),10)-1) * oPaging.iLength;
                            fnDraw( oSettings );
                        } );
                }

                // Add / remove disabled classes from the static elements
                if ( oPaging.iPage === 0 ) {
                    $('li:lt(2)', an[i]).addClass('disabled');
                } else {
                    $('li:lt(2)', an[i]).removeClass('disabled');
                }

                if ( oPaging.iPage === oPaging.iTotalPages-1 || oPaging.iTotalPages === 0 ) {
                    $('.next', an[i]).addClass('disabled');
                    $('li:last', an[i]).addClass('disabled');
                } else {
                    $('.next', an[i]).removeClass('disabled');
                    $('li:last', an[i]).removeClass('disabled');
                }
            }
        }
    }
} );


/*
 * TableTools Bootstrap compatibility
 * Required TableTools 2.1+
 */
if ( $.fn.DataTable.TableTools ) {
    // Set the classes that TableTools uses to something suitable for Bootstrap
    $.extend( true, $.fn.DataTable.TableTools.classes, {
        "container": "DTTT btn-group",
        "buttons": {
            "normal": "btn",
            "disabled": "disabled"
        },
        "collection": {
            "container": "DTTT_dropdown dropdown-menu",
            "buttons": {
                "normal": "",
                "disabled": "disabled"
            }
        },
        "print": {
            "info": "DTTT_print_info modal"
        },
        "select": {
            "row": "active"
        }
    } );

    // Have the collection use a bootstrap compatible dropdown
    $.extend( true, $.fn.DataTable.TableTools.DEFAULTS.oTags, {
        "collection": {
            "container": "ul",
            "button": "li",
            "liner": "a"
        }
    } );
}