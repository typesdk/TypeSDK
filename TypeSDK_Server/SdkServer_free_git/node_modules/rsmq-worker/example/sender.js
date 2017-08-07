(function() {
  var RSMQWorker, worker;

  RSMQWorker = require("../.");

  worker = new RSMQWorker("myqueue", {
    autostart: false
  });

  worker.on("ready", function() {
    var i, len, msg, ref;
    ref = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
    for (i = 0, len = ref.length; i < len; i++) {
      msg = ref[i];
      console.log("SEND", msg);
      worker.send(msg);
    }
  });

}).call(this);
