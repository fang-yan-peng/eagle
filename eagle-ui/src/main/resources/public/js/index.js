$(function() {
    $("#content").load("/global/registry_center.html");
    $("#reg-center").click(function() {
        $("#content").load("/global/registry_center.html");
    });
    $("#client-status").click(function() {
        $("#content").load("/status/client/services_status_overview.html");
    });
    $("#server-status").click(function() {
        $("#content").load("/status/server/servers_status_overview.html");
    });
    $("#help").click(function() {
        $("#content").load("/help/help.html", null, function(){
            doLocale();
        });
    });
    switchLanguage();
});
