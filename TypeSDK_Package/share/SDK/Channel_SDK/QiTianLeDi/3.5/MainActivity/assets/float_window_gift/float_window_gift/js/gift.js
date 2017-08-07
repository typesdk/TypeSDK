(function() {
    var keys, template;
    template = '<div class="item row"><div class="grid-cwf"><a href="gift_show.html?id=ggame_id&goback=gift"><div class="col-main"><div class="main-wrap"><div class="icon col-xs-3 col-sm-2 col-md-2 col-lg-1"><img class="img-responsive" src="gicon" alt="ggame_name"></div><div class="app-info"><ul class="gift"><li class="title">ggame_name</li><li>礼包种类：gtotal</li></ul></div></div></div><div class="col-sub text-right"><div class="arrow"><div class="sl-hvalign"><div class="sl-hvalign-cnt"><div class="sl-hvalign-cnt-inner"><i></i></div></div></div></div></div></a></div></div>';
    keys = [ "icon", "total", "game_name", "game_id" ];
    $.ajax({
        type: "get",
        async: false,
        dataType: "jsonp",
        url: "http://api.xuha.com/ios/gift?act=l&jsoncallback=?",
        success: function(r) {
            var html;
            html = "";
            $.each(r, function(key, game) {
                var tmp;
                tmp = template;
                $.each(keys, function(k, v) {
                    var e, val;
                    e = new RegExp("(g" + v + ")", "g");
                    val = v === "icon" || v === "url" ? game[v] : game[v];
                    return tmp = tmp.replace(e, val);

                });
                
                return html += tmp;
            });
            return $(".container-fluid").append(html);
        },
        error: function() {
            return alert("request timeout");
        }
    });
}).call(this);