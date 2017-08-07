(function() {
    var keys, vgid, template;
    template = '<tr><td class="gift_title" >gname</td><td width="100" valign="middle" rowspan="2"><p><a class="btn btn-success" data-toggle="modal" data-target=".bs-example-modal-sm" gameid=ggame_id giftid=gid href="#">立即领取</a></p><p class="stock" >剩余: gremaining</p></td></tr><tr class="gift_bd"><td><span >奖励内容: </span>gintro</td></tr>';
    keys = [ "name", "intro", "remaining", "game_id", "id"];
    var URLParams = new Array();
    var aParams = document.location.search.substr(1).split('&');
    for (i=0; i < aParams.length ; i++){
      var aParam = aParams[i].split('=');
      URLParams[aParam[0]] = aParam[1];
    }
    vid2=URLParams["id"];
    vgoback=URLParams["goback"];
    typed=(URLParams["type"]==null || URLParams["type"]=='')?'':'?type='+URLParams["type"];

    vurl=(URLParams["type"]==null || URLParams["type"]=='')?'gift.html':'game_show.html?id='+vid2+'&goback=gift';
    vid=(URLParams["id"]==null || URLParams["id"]=='')?'':'&id='+URLParams["id"];
    $(".goback").click(function() {
        window.location.href =vurl;
    });
    $.ajax({
        type: "get",
        async: false,
        dataType: "jsonp",
        url: "http://api.xuha.com/ios/gift?act=l&gid="+vid2+"&jsoncallback=?",
        success: function(r) {
            var html;
            var end_len;
            html = "";
            if (r==""||r==null) {
                alert('此游戏暂无礼包~');
                window.location.href =vurl;
            };
              if (screen.width <= 320){
                  end_len=26;
              }else if(320<screen.width && screen.width<= 360){
                  end_len=34;
              }else{
                  end_len=100;
              }
            $.each(r, function(key, game) {
                var tmp;
                tmp = template;
                diff=game.intro.substring(0, end_len);
                intro_len=game.intro.length;
                diff_len=diff.length;
                if(diff_len<intro_len){
                    game.intro=diff + '...';
                }
                
                $("h3.title ").html(game.game_name);
                $.each(keys, function(k, v) {
                    var e, val;
                    e = new RegExp("(g" + v + ")", "g");
                    val = v === "icon" || v === "url" ? game[v] : game[v];
                    return tmp = tmp.replace(e, val);
                });
                return html += tmp;
            });
            return $("#tbody").append(html);
        },
        error: function() {
            return alert("request timeout");
        }
    });
}).call(this);