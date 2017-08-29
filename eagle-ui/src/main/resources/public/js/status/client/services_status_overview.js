$(function() {
    authorityControl();
    renderServicesOverview();
    bindButtons();
});

function renderServicesOverview() {
    var jsonData = {
        cache: false,
        search: true,
        showRefresh: true,
        showColumns: true
    };
    var activated = false;
    $.ajax({
        url: "/registry-center/activated",
        async: false,
        success: function(data) {
            activated = data;
        }
    });
    if (activated) {
        jsonData.url = "/service/getClientsBriefInfo";
    }
    $("#services-status-overview-tbl").bootstrapTable({
        columns: jsonData.columns,
        url: jsonData.url,
        cache: jsonData.cache
    }).on("all.bs.table", function() {
        doLocale();
    });
}

function statusFormatter(value, row) {
    switch(value) {
        case "OK":
            return "<span class='label label-success' data-lang='status-ok'></span>";
            break;
        case "DISABLED":
            return "<span class='label label-warning' data-lang='status-disabled'></span>";
            break;
        case "SHARDING_FLAG":
            return "<span class='label label-info' data-lang='status-sharding-flag'></span>";
            break;
        case "CRASHED":
            return "<span class='label label-default' data-lang='status-crashed'></span>";
            break;
    }
}

function generateOperationButtons(val, row) {
    var modifyButton = "<button operation='modify-service' class='btn-xs btn-primary' service-name='" + row.serviceName + "' protocol='"+row.protocol+"' host='"+row.host+"' process='"+row.process+"' data-lang='operation-update'></button>";
    var removeButton = "<button operation='remove-service' class='btn-xs btn-danger' service-name='" + row.serviceName + "' protocol='"+row.protocol+"' host='"+row.host+"' process='"+row.process+"' data-lang='operation-remove'></button>";
    var operationTd = "";
    if ("OK" === row.status) {
        operationTd = modifyButton + "&nbsp;";
    }else if ("CRASHED" === row.status) {
        operationTd = removeButton + "&nbsp;";
    }
    return operationTd;
}

function bindButtons() {
    bindModifyButton();
    bindRemoveButton();
}

function bindModifyButton() {
    $(document).off("click", "button[operation='modify-service'][data-toggle!='modal']");
    $(document).on("click", "button[operation='modify-service'][data-toggle!='modal']", function(event) {
        var serviceName = $(event.currentTarget).attr("service-name");
        var protocol = $(event.currentTarget).attr("protocol");
        var host = $(event.currentTarget).attr("host");
        var process = $(event.currentTarget).attr("process");
        $.ajax({
            url: "/service/client/config",
            type:"POST",
            data:"host="+host+":"+process+"&serviceName="+serviceName+"&protocol="+protocol,
            success: function(data) {
                if (null !== data) {
                    $(".box-body").remove();
                    $('#update-service-body').load('/status/client/service_config.html', null, function() {
                        doLocale();
                        $('#data-update-service').modal({backdrop : 'static', keyboard : true});
                        renderJob(data);
                        $("#service-overviews-name").text(serviceName);
                    });
                }
            }
        });
    });
}


function bindRemoveButton() {
    $(document).off("click", "button[operation='remove-service'][data-toggle!='modal']");
    $(document).on("click", "button[operation='remove-service'][data-toggle!='modal']", function(event) {
        var serviceName = $(event.currentTarget).attr("service-name");
        var protocol = $(event.currentTarget).attr("protocol");
        showDeleteConfirmModal();
        $(document).off("click", "#confirm-btn");
        $(document).on("click", "#confirm-btn", function() {
            $.ajax({
                url: "/service/client/config/delete",
                type: "POST",
                data:"serviceName="+serviceName+"&protocol="+protocol,
                success: function() {
                    $("#confirm-dialog").modal("hide");
                    $(".modal-backdrop").remove();
                    $("body").removeClass("modal-open");
                    refreshServiceNavTag();
                    $("#services-status-overview-tbl").bootstrapTable("refresh");
                }
            });
        });
    });
}

function renderJob(data) {
    $("#protocol").attr("value", data.protocol);
    $("#serialization").attr("value", data.serialization);
    $("#group").attr("value", data.group);
    $("#service-name").attr("value", data.serviceName);
    $("#host").attr("value", data.host);
    $("#process").attr("value",data.process);
    $("#codec").attr("value", data.codec);
    $("#actives").attr("value", data.actives);
    $("#max-content-length").attr("value", data.maxContentLength);
    $("#actives-wait").attr("value", data.activesWait);
    $("#check").attr("checked", data.check);
    $("#use-native").attr("checked", data.useNative);
    $("#compress").attr("checked", data.compress);
    $("#use-default").attr("checked", data.useDefault);
    $("#cluster").attr("value", data.cluster);
    $("#heartbeat-factory").attr("value", data.heartbeatFactory);
    $("#heartbeat").attr("value", data.heartbeat);
    $("#version").attr("value", data.version);
    $("#stats-log").attr("value", data.statsLog);
    $("#retries").attr("value", data.retries);
    $("#callback-thread").attr("value", data.callbackThread);
    $("#callback-queue-size").attr("value", data.callbackQueueSize);
    $("#callback-wait-time").attr("value", data.callbackWaitTime);
    $("#request-timeout").attr("value", data.requestTimeout);
    $("#connect-timeout").attr("value", data.connectTimeout);
    $("#idle-time").attr("value", data.idleTime);
    $("#min-client-connection").attr("value", data.minClientConnection);
    $("#max-client-connection").attr("value", data.maxClientConnection);
    $("#max-invoke-error").attr("value", data.maxInvokeError);
    $("#loadbalance").attr("value", data.loadbalance);
    $("#ha-strategy").attr("value", data.haStrategy);
    $("#max-lifetime").attr("value", data.maxLifetime);
    $("#callback").attr("value", data.callback);
    $("#mock").attr("value", data.mock);
}
