<%@ Page Title="渠道管理" Language="C#" MasterPageFile="~/Admin.Master" AutoEventWireup="true" CodeBehind="Platform.aspx.cs" Inherits="SDKPackage.PJConfig.Platform" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <script src="/vendors/jquery.form/jquery.form.js"></script>
    <script type="text/javascript">
        $(function () {
            $("#addPlatform").click(function () {
                var strhref = "AddPlatform.aspx";
                var strParam = "?myversionid=" + $("#MainContent_ddlSdkVersionList").val();
                strParam = strParam + "&platformid=" + $("#MainContent_ddlPlatforms").val();
                $("#hfreturnVal").val("");
                openfile(strhref, strParam);
            });
            $("#addSdkVersion").click(function () {
                var strhref = "AddMyVersion.aspx";
                var strParam = "?myversionid=" + $("#MainContent_ddlSdkVersionList").val();
                strParam = strParam + "&platformid=" + $("#MainContent_ddlPlatforms").val();
                $("#hfreturnVal").val("");
                openfile(strhref, strParam);
            });
            $("#itemPlaceholderContainer input:file").change(function () {
                //return;
                var fileName = $(this).val();
                //判断上传文件的后缀名  
                var strExtension = fileName.substr(fileName.lastIndexOf('.') + 1);
                if (strExtension != 'jpg' && strExtension != 'gif'
                    && strExtension != 'png' && strExtension != 'bmp') {
                    alert("请选择图片文件");
                    return;
                }
                ajaxFileUpload();
            });
        })

        function ajaxFileUpload() {
            $("#ctl01").ajaxSubmit({
                url: '/Upload_Img.ashx?imgurl=platform',
                beforeSubmit: function () {
                    $("#gameicon").attr("src", "/img/scz.gif");
                },
                success: function (json) {
                    json = eval("(" + json + ")");
                    if (json.success == "error") {
                        $("#gameicon").attr("src", "/img/upimg.jpg");
                        alert(json.msg);
                    }
                    else if (json.success == "success") {
                        $("#gameicon").attr("src", json.imgsrc);
                        $("#MainContent_hfPlatfomrIcon").val(json.imgsrc);
                    }
                }
            });
            return false;
        }


        function IsPlatformIconEdit(obj, id) {
            var platformIcon = $(obj).next("input:hidden").val();
            var upPlatformIcon = $("#MainContent_hfPlatfomrIcon").val();
            SetVersion(obj);//插入hidden 值  ddl版本选中项
            $("#MainContent_hfPlatfomrIcon").val("");
            if (upPlatformIcon != "" && upPlatformIcon != platformIcon)//更新了角标
            {
                UpdatePlatformIcon(id, upPlatformIcon);
            }
        }

        function SetVersion(obj) {
            var val = $(obj).parent("td").parent("tr").children("td").eq(2).children("select").val();
            if (val == null || val == undefined || val == "") val = 0;
            $("#MainContent_hfVersionVal").val(val);
        }

        function UpdatePlatformIcon(id, iconpath) {
            $.ajax({
                contentType: "application/json",
                async: false,
                url: "/WS/WSNativeWeb.asmx/UpdatePlatformIconName",
                data: "{platformID:'" + id + "',platformIconName:'" + iconpath + "'}",
                type: "POST",
                dataType: "json", success: function (json) {
                }
            });
        }


        var timer;
        var winOpen;
        function openfile(obj, param) {
            $("#hfreturnVal").val("");
            var str_href = obj + param;
            var w = $(window).width() * 0.3;
            var h = $(window).height() * 0.2;
            winOpen = window.open(str_href, '上传文件', 'height=570,width=800,top=' + h + ',left=' + w + ',toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
            timer = window.setInterval("IfWindowClosed()", 500);
        }

        function IfWindowClosed() {

            if (winOpen.closed == true) {
                var hf_val = $("#hfreturnVal").val();
                //alert("子页面关闭=>返回值：" + hf_val);
                window.clearInterval(timer);
                if (hf_val == "success") {
                    window.location.reload();
                }
            }
        }
    </script>
    <!-- Datatables -->
    <link href="/vendors/datatables.net-bs/css/dataTables.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-buttons-bs/css/buttons.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-fixedheader-bs/css/fixedHeader.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-responsive-bs/css/responsive.bootstrap.min.css" rel="stylesheet">
    <link href="/vendors/datatables.net-scroller-bs/css/scroller.bootstrap.min.css" rel="stylesheet">

    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="x_panel">
                <div class="x_title">
                    <h2>渠道管理-客户端配置</h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">
                    <div class="form-inline">
                        <div class="form-group">
                            <label class="control-label">平台</label>
                            <asp:DropDownList ID="ddlPlatforms" runat="server" CssClass="form-control" AutoPostBack="True" OnSelectedIndexChanged="ddlPlatforms_SelectedIndexChanged">
                                <asp:ListItem Value="1">Android</asp:ListItem>
                                <asp:ListItem Value="2">IOS</asp:ListItem>
                            </asp:DropDownList>
                            <label class="control-label">SDK版本</label>
                            <asp:DropDownList ID="ddlSdkVersionList" runat="server" CssClass="form-control" AutoPostBack="True"></asp:DropDownList>
                        </div>
                        <div class="form-group">
                            <a id="addSdkVersion" class="btn-primary btn">添加SDK版本  </a>
                        </div>
                        <div class="form-group">
                            <a id="addPlatform" class="btn-primary btn">添加新渠道  </a>
                        </div>
                        <div class="form-group">
                            <a class="btn btn-primary" href="PlatformVersion.aspx">渠道版本管理</a>
                        </div>
                        <div class="form-group">
                            <asp:Button ID="Button1" runat="server" Text="全部维护" CssClass="btn btn-primary" />
                        </div>
                        <div class="form-group">
                            <asp:Button ID="Button2" runat="server" Text="取消维护" CssClass="btn btn-primary" />
                        </div>
                    </div>
                    <hr />
                    <div class="row">
                        <asp:ListView ID="ListView1" runat="server" DataKeyNames="Id" DataSourceID="SqlDataSourcePlatform" OnItemCommand="ListView1_ItemCommand">
                            <EmptyDataTemplate>
                                <table runat="server" style="">
                                    <tr>
                                        <td>没有获得渠道数据。</td>
                                    </tr>
                                </table>
                            </EmptyDataTemplate>

                            <ItemTemplate>
                                <tr>
                                    <td>
                                        <asp:Label ID="PlatformDisplayNameLabel" runat="server" Text='<%# Eval("PlatformDisplayName") %>' />
                                    </td>
                                    <td>
                                        <asp:Label ID="PlatformNameLabel" runat="server" Text='<%# Eval("PlatformName") %>' />
                                    </td>
                                    <td>
                                        <asp:Label ID="SdkVersionLabel" runat="server" Text='<%# Eval("SdkVersion") %>' />
                                    </td>
                                    <td  data-toggle="modal" data-target='.<%# Eval("PlatformName") %>-icon-modal-sm'>
                                        <img src='<%#Eval("platformicon").ToString()==""?"/img/upimg.jpg":Eval("platformicon").ToString() %>' style="width: 20px; height: 20px;" />
                                    </td>
                                    <td>
                                         <asp:Button ID="EditButton" runat="server" CommandName="Edit" class="btn btn-primary btn-sm fa" Text="&#xf044; 编辑" />
                                    </td>
                                    <td>
                                        <asp:Button ID="DeteteButton" runat="server" CommandName="Delete" class="btn btn-danger btn-sm fa" Text="&#xf1f8; 删除" OnClientClick="return confirm('确定要删除数据吗？')" /></td>
                                    <td>
                                        <asp:LinkButton ID="LinkButtonNullity" runat="server" CommandName="nullity" CommandArgument='<%#Eval("dpfid")+","+Eval("nullity") %>'><%#(byte)Eval("Nullity")==0?"<div class='btn btn-success btn-sm fa'><i class='fa fa-cogs'></i> 使用中</div>":"<div class='btn btn-warning btn-sm fa'><i class='fa fa-exclamation-triangle'></i> 维护中</div>" %></asp:LinkButton>
                                    </td>
                                </tr>
                                            <div <div class='modal fade <%# Eval("PlatformName") %>-icon-modal-sm' tabindex="-1" role="dialog" aria-hidden="true">
                                            <div class="modal-dialog modal-sm">
                                              <div class="modal-content">

                                                <div class="modal-header">
                                                  <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span>
                                                  </button>
                                                  <h4 class="modal-title" id="myModalLabel2"><%# Eval("PlatformDisplayName") %> 角标</h4>
                                                </div>
                                                <div class="modal-body">
                                                <img src='<%#Eval("platformicon").ToString()==""?"/img/upimg.jpg":Eval("platformicon").ToString() %>' style="width: 100%" />
                                                </div>

                                              </div>
                                            </div>
                                          </div>
                            </ItemTemplate>

                            <LayoutTemplate>
                                <table id="itemPlaceholderContainer" class="table table-striped jambo_table">
                                    <caption>渠道列表</caption>
                                    <thead>
                                        <tr runat="server">
                                            <th>渠道名称</th>
                                            <th>渠道编号</th>
                                            <th>最高支持版本</th>
                                            <th>角标</th>
                                            <th>编辑</th>
                                            <th>删除</th>
                                            <th>状态</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr id="itemPlaceholder" runat="server">
                                        </tr>
                                    </tbody>
                                </table>
                            </LayoutTemplate>
                            <EditItemTemplate>
                                <tr>
                                    <td>
                                        <asp:Label ID="PlatformDisplayNameLabel" runat="server" Text='<%# Eval("PlatformDisplayName") %>' />
                                    </td>
                                    <td>
                                        <asp:Label ID="PlatformNameLabel" runat="server" Text='<%# Eval("PlatformName") %>' />
                                    </td>
                                    <td>
                                        <asp:DropDownList ID="DropDownListPlatformVersion" runat="server" DataSource='<%#GetDropDownListPlatformVersionDataSource(Eval("dpfid").ToString(),Eval("SdkVersionID").ToString()) %>' DataTextField="version" DataValueField="id"></asp:DropDownList>
                                    </td>
                                    <td>
                                        <div style="position: relative; width: 30px; height: 30px; overflow: hidden;">
                                            <img src='<%#Eval("platformicon").ToString()==""?"/img/upimg.jpg":Eval("platformicon") %>' style="width: 30px; height: 30px;" id="gameicon" />
                                            <asp:FileUpload ID="GameIocnFileUpload" runat="server" CssClass="form-control" Style="cursor: pointer; padding: 0; height: 30px; width: 30px; filter: alpha(opacity=0); -moz-opacity: 0; -khtml-opacity: 0; opacity: 0; position: absolute; top: 0; left: 0;" />
                                        </div>
                                    </td>
                                    <td>
                                        <asp:Button ID="UpdateButton" runat="server" CommandName="Update" class="btn btn-success btn-sm fa" Text="&#xf0c7; 保存" Style="margin-right: 20px;" OnClientClick=<%# "IsPlatformIconEdit(this,'" + Eval("dpfid") + "') "%> />
                                        <input type="hidden" value='<%# Eval("PlatformIcon") %>' />
                                        <asp:Button ID="CancelButton" runat="server" CommandName="Cancel" class="btn btn-default btn-sm fa" Text="&#xf112; 取消" />
                                    </td>
                                    <td>
                                        <asp:Button ID="DeteteButton" runat="server" CommandName="Delete" class="btn btn-danger btn-sm fa" Text="&#xf1f8; 删除" OnClientClick="return confirm('确定要删除数据吗？')" /></td>
                                    <td>
                                        <asp:LinkButton ID="LinkButtonNullity" runat="server" CommandName="nullity" CommandArgument='<%#Eval("dpfid")+","+Eval("nullity") %>'><%#(byte)Eval("Nullity")==0?"<span class='btn btn-success btn-sm fa'><i class='fa fa-cogs'></i> 使用中</span>":"<span class='btn btn-warning btn-sm fa'><i class='fa fa-exclamation-triangle'></i> 维护中</span>" %></asp:LinkButton>
                                    </td>

                                </tr>
                            </EditItemTemplate>
                        </asp:ListView>
                        <asp:SqlDataSource ID="SqlDataSourcePlatform" runat="server" ConnectionString="<%$ ConnectionStrings:SdkPackageConnString %>"
                            SelectCommand="sdk_getPlatformListInfo" SelectCommandType="StoredProcedure"
                            UpdateCommand="Update [sdk_Platform] set SdkVersion=@SdkVersion where ID=@ID" UpdateCommandType="Text" DeleteCommand="delete from sdk_Platform where MyVersionID=@SdkVersion and [SystemID]=@Platforms and id=@ID">
                            <SelectParameters>
                                <asp:ControlParameter ControlID="ddlPlatforms" Name="SystemID" PropertyName="SelectedValue" Type="Int32" />
                                <asp:ControlParameter ControlID="ddlSdkVersionList" Name="SdkVersion" PropertyName="SelectedValue" Type="Int32" />
                            </SelectParameters>
                            <UpdateParameters>
                                <asp:ControlParameter ControlID="hfVersionVal" Name="SdkVersion" PropertyName="Value" Type="Int32" />
                                <asp:Parameter Name="ID" DbType="Int32" />
                            </UpdateParameters>
                            <DeleteParameters>
                                <asp:Parameter Name="ID" DbType="Int32" />
                                <asp:ControlParameter ControlID="ddlPlatforms" Name="Platforms" PropertyName="SelectedValue" Type="String" />
                                <asp:ControlParameter ControlID="ddlSdkVersionList" Name="SdkVersion" PropertyName="SelectedValue" Type="String" />
                            </DeleteParameters>
                        </asp:SqlDataSource>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <input id="hfreturnVal" type="hidden" />
    <asp:HiddenField ID="hfPlatfomrIcon" runat="server" Value="" />
    <asp:HiddenField ID="hfVersionVal" runat="server" Value="0" />

    <!-- Datatables -->
    <script src="/vendors/datatables.net/js/jquery.dataTables.min.js"></script>
    <script src="/vendors/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
    <script src="/vendors/datatables.net-buttons-bs/js/buttons.bootstrap.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/buttons.flash.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/buttons.html5.min.js"></script>
    <script src="/vendors/datatables.net-buttons/js/buttons.print.min.js"></script>
    <script src="/vendors/datatables.net-fixedheader/js/dataTables.fixedHeader.min.js"></script>
    <script src="/vendors/datatables.net-keytable/js/dataTables.keyTable.min.js"></script>
    <script src="/vendors/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
    <script src="/vendors/datatables.net-responsive-bs/js/responsive.bootstrap.js"></script>
    <script src="/vendors/datatables.net-scroller/js/datatables.scroller.min.js"></script>
    <script src="/vendors/jszip/dist/jszip.min.js"></script>
    <script src="/vendors/pdfmake/build/pdfmake.min.js"></script>
    <script src="/vendors/pdfmake/build/vfs_fonts.js"></script>

    <!-- Datatables -->
    <script>
        $(document).ready(function() {
        $('#itemPlaceholderContainer').dataTable({
            "paging": false,
            "order": [[ 4, "desc" ]],
            "sPaginationType" : "full_numbers",
            "aoColumnDefs" : [ { "bSortable": false, "aTargets": [0,2,3,5,6] } ],
            "oLanguage" : {
                "sLengthMenu": "每页显示 _MENU_ 条记录",
                "sZeroRecords": "抱歉， 没有找到",
                "sInfo": "从 _START_ 到 _END_ /共 _TOTAL_ 条数据",
                "sInfoEmpty": "没有数据",
                "sInfoFiltered": "(从 _MAX_ 条数据中检索)",
                "sZeroRecords": "没有检索到数据",
                "sSearch": "搜索:",
                "oPaginate": {
                "sFirst": "首页",
                "sPrevious": "前一页",
                "sNext": "后一页",
                "sLast": "尾页"
                }
            }
        });
        });
    </script>
</asp:Content>
