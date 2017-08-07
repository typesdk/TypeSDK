<%@ Page Title="" Language="C#" AutoEventWireup="true" MasterPageFile="/Windows.Master" CodeBehind="AddPlatform.aspx.cs" Inherits="SDKPackage.PJConfig.AddPlatform" %>

<asp:Content ID="Content1" ContentPlaceHolderID="MainContent" runat="server">
    <script src="/vendors/jquery.form/jquery.form.js"></script>
    <script type="text/javascript">
        $(function () {
            $('#MainContent_GameIocnFileUpload').change(function () {
                var fileName = $("#MainContent_GameIocnFileUpload").val();
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
                    alert(123);
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
    </script>
                <div class="x_title">
                    <h2>上传项目文件
                    </h2>
                    <ul class="nav navbar-right panel_toolbox">
                      <li><a class="collapse-link"><i class="fa fa-chevron-up"></i></a>
                      </li>
                    </ul>
                <div class="clearfix"></div>
                </div>
                <div class="x_content">
    <div class="row">
        <div class="col-md-12 col-sm-12 col-xs-12">
            <div class="form-group">
                <label for="txtPlatformName" class="control-label col-md-3 col-sm-3 col-xs-12">渠道编号<span class="required">*</span></label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <asp:TextBox ID="txtPlatformName" runat="server" CssClass="form-control" placeholder="请输入渠道编号" required="required"></asp:TextBox>
                </div>
                <div class="clear"></div>
            </div>
            <div class="form-group">
                <label for="txtPlatformDisplayName" class="control-label col-md-3 col-sm-3 col-xs-12">渠道名称<span class="required">*</span></label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <asp:TextBox ID="txtPlatformDisplayName" runat="server" CssClass="form-control" placeholder="请输入渠道名称" required="required"></asp:TextBox>
                </div>
                <div class="clear"></div>
            </div>
            <div class="form-group">
                <label for="txtSdkVersion" class="control-label col-md-3 col-sm-3 col-xs-12">渠道SDK版本<span class="required">*</span></label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                    <asp:TextBox ID="txtSdkVersion" runat="server" CssClass="form-control" placeholder="请输入渠道SDK版本" required="required"></asp:TextBox>
                </div>
                <div class="clear"></div>
            </div>
            <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" >渠道角标(512px)<span class="required">*</span></label>
                <div class="col-md-6 col-sm-6 col-xs-12" style="position: relative;">
                    <div style="position: relative; width: 60px; height: 60px; overflow: hidden;">
                        <img src="../img/upimg.jpg" style="width: 60px; height: 60px;" id="gameicon" />
                        <asp:FileUpload ID="GameIocnFileUpload" runat="server" CssClass="form-control" Style="cursor: pointer; padding: 0; height: 80px; width: 80px; filter: alpha(opacity=0); -moz-opacity: 0; -khtml-opacity: 0; opacity: 0; position: absolute; top: 0;" />
                    </div>
                </div>
                <div class="clear"></div>
            </div>
            <%--            <div class="form-group">
                <label for="firstname" class="col-sm-2 control-label">渠道状态</label>
                <div class="col-sm-10">
                    <asp:DropDownList ID="DropDownList1" runat="server" CssClass="form-control" style="width:150px;"></asp:DropDownList>
                </div>
            </div>--%>
            <div class="form-group text-center"">
                <asp:Button ID="btnSubmit" runat="server" Text="  提交  " OnClick="btnSubmit_Click" CssClass="btn-primary btn" />
                <asp:Label ID="lblLog" runat="server" Text="" Style="margin-left: 20px; color: #f00"></asp:Label>
            </div>
        </div>
    </div>
                </div>
    <asp:HiddenField ID="hfPlatformIcon" runat="server" />
</asp:Content>
