<%@ Page Language="C#" AutoEventWireup="true" MasterPageFile="/Windows.Master" CodeBehind="GameVersionAdd.aspx.cs" Inherits="SDKPackage.PJPackage.GameVersionAdd" %>

<asp:Content ID="BodyContent" ContentPlaceHolderID="MainContent" runat="server">

    <script type="text/javascript">
        window.onload = function () {
            if (!window.applicationCache) {
                $(".info").html("你的浏览器版本过低，上传文件功能不能使用，请更换高版本浏览器！");
                $(".info").show();
                $(".content").hide();
            }
        }
        function uploadFile() {
            <%if (!flag)
              {%>
            alert("参数不正确！");
            return;
            <%}%>
            var lablename = $("#MainContent_TextBoxVersionLabel").val();
            if (lablename == undefined || lablename == null || lablename == "") {
                alert("标签不能为空！"); return;
            }

            var filename = $("#fileToUpload").val();
            if (filename == undefined || filename == null || filename == "") {
                alert("请选择上传文件！"); return;
            }

            $("#btnUpload").hide();
            var file = document.getElementById('fileToUpload').files[0];
            var gameid = '<%= gameid%>';
            var gamename = '<%= gamename%>';
            var platform = '<%= platform%>';
            var gamenamespell = '<%= gamenamespell%>';

            var fd = new FormData();
            fd.append("fileToUpload", file); //document.getElementById('fileToUpload').files[0]);

            var xhr = new XMLHttpRequest();
            xhr.upload.addEventListener("progress", uploadProgress, false);
            xhr.addEventListener("load", uploadComplete, false);
            xhr.addEventListener("error", uploadFailed, false);
            xhr.addEventListener("abort", uploadCanceled, false);
            xhr.open("POST", "UpdateGameZip.ashx?gameid=" + gameid + "&gamename=" + gamename + "&platform=" + platform + "&gamenamespell=" + gamenamespell + "&lablename=" + lablename);//修改成自己的接口
            xhr.send(fd);
            window.opener.document.getElementById("hfreturnVal").value = "success";
        }

        function fileSelected() {
            var file = document.getElementById('fileToUpload').files[0];
            if (file) {
                var siff = file.name.toString().substring(file.name.toString().lastIndexOf('.') + 1, file.name.toString().lastIndexOf('.') + 4);
                if (siff != "zip") {
                    alert("上传文件必须为zip");
                    $("#fileToUpload").val("");
                    return;
                }

                var fileSize = 0;
                if (file.size > 1024 * 1024)
                    fileSize = (Math.round(file.size * 100 / (1024 * 1024)) / 100).toString() + 'MB';
                else
                    fileSize = (Math.round(file.size * 100 / 1024) / 100).toString() + 'KB';
            }
        }
        function uploadProgress(evt) {
            if (evt.lengthComputable) {
                var percentComplete = Math.round(evt.loaded * 100 / evt.total);
                var zipSize = evt.total / 1024 / 1024;
                document.getElementById('progressNumber').innerHTML = (percentComplete * zipSize / 100).toFixed(2) + "M/" + zipSize.toFixed(2) + "M"; //percentComplete.toString() + '%';
                if (percentComplete * zipSize / 100 == zipSize)
                    document.getElementById('progressNumber').innerHTML = "上传完毕，文件正在处理......";
            }
            else {
            }
        }

        function uploadComplete(evt) {
            $("#LogLabel").text(evt.target.responseText);
            var zipSize = evt.total / 1024 / 1024;
            if (zipSize.toString().indexOf(".") > 0) {
                zipSize = zipSize.toString().substring(0, zipSize.toString().indexOf(".") + 3);
            }
            document.getElementById('progressNumber').innerHTML = "";

            $("#btnUpload").show();
        }

        function uploadFailed(evt) {
            alert("There was an error attempting to upload the file.");
            $("#btnUpload").show();
        }

        function uploadCanceled(evt) {
            alert("The upload has been canceled by the user or the browser dropped the connection.");
            $("#btnUpload").show();
        }
    </script>
        <div class="col-sm-6">
            <div class="form-horizontal">
                <fieldset>
                    <legend>项目上传</legend>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">文件</label>
                        <div class="col-sm-10">
                            <input type="file" name="fileToUpload" id="fileToUpload" onchange="fileSelected();" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">标签</label>
                        <div class="col-sm-10">
                            <asp:TextBox ID="TextBoxVersionLabel" CssClass="form-control" runat="server" placeholder="请输入标签" required="required" MaxLength="15"></asp:TextBox>
                        </div>
                    </div>
                    <div class="form-group">
                        <input type="button" onclick="uploadFile()" value="上  传" class="col-md-offset-2 btn btn-primary" id="btnUpload" />
                        <div id="progressNumber"></div>
                    </div>
                </fieldset>
            </div>
            <asp:Label ID="LogLabel" runat="server" Text="" ForeColor="Red" Style="margin-left: 30%;"></asp:Label>
            <asp:HiddenField ID="hfFlag" runat="server" />
        </div>

    <div class="col-sm-6">

        <h3>操作说明</h3>
        <p>1. 需要将导出的Android项目目录改名为Game,并压缩为zip文件。</p>
        <p>2. 选择游戏项目文件(zip格式)</p>
        <p>3. 点击“上传”。</p>
        <p>建议使用Firefox、Chrome浏览器，IE仅支持ie9以上版本。</p>

    </div>
    <div id="fileName"></div>
    <div id="fileSize"></div>
    <div id="fileType"></div>
</asp:Content>
