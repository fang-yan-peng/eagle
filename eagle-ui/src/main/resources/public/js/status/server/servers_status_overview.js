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
        jsonData.url = "/service/getServersBriefInfo";
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
        case "CRASHED":
            return "<span class='label label-default' data-lang='status-crashed'></span>";
            break;
    }
}

function generateOperationButtons(val, row) {
    var modifyButton = "<button operation='modify-service' class='btn-xs btn-primary' service-name='" + row.serviceName + "' protocol='"+row.protocol+"' host='"+row.host+"' process='"+row.process+"' data-lang='operation-update'></button>";
    var disableButton = "<button operation='disable-service' class='btn-xs btn-warning' service-name='" + row.serviceName + "' protocol='"+row.protocol+"' host='"+row.host+"' process='"+row.process+"' data-lang='operation-disable'></button>";
    var enableButton = "<button operation='enable-service' class='btn-xs btn-success' service-name='" + row.serviceName + "' protocol='"+row.protocol+"' host='"+row.host+"'  process='"+row.process+"' data-lang='operation-enable'></button>";
    var removeButton = "<button operation='remove-service' class='btn-xs btn-danger' service-name='" + row.serviceName + "' protocol='"+row.protocol+"' host='"+row.host+"' process='"+row.process+"' data-lang='operation-remove'></button>";
    var operationTd = modifyButton + "&nbsp;";
    if ("OK" === row.status) {
        operationTd = operationTd + "&nbsp;" + disableButton + "&nbsp;";
    }
    if ("DISABLED" === row.status) {
        operationTd = operationTd + "&nbsp;" + enableButton + "&nbsp;";
    }
    if ("CRASHED" === row.status) {
        operationTd = removeButton + "&nbsp;";
    }

    return operationTd;
}

function bindButtons() {
    bindModifyButton();
    bindDisableButton();
    bindEnableButton();
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
            url: "/service/server/config",
            type:"POST",
            data:"host="+host+":"+process+"&serviceName="+serviceName+"&protocol="+protocol,
            success: function(data) {
                if (null !== data) {
                    $(".box-body").remove();
                    $('#update-service-body').load('/status/server/server_config.html', null, function() {
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

function bindDisableButton() {
    $(document).off("click", "button[operation='disable-service'][data-toggle!='modal']");
    $(document).on("click", "button[operation='disable-service'][data-toggle!='modal']", function(event) {
        var serviceName = $(event.currentTarget).attr("service-name");
        var protocol = $(event.currentTarget).attr("protocol");
        var host = $(event.currentTarget).attr("host");
        var process = $(event.currentTarget).attr("process");
        $.ajax({
            url: "/service/server/disable",
            type: "POST",
            data:"host="+host+":"+process+"&serviceName="+serviceName+"&protocol="+protocol,
            success: function() {
                showSuccessDialog();
                $("#services-status-overview-tbl").bootstrapTable("refresh");
            }
        });
    });
}

function bindEnableButton() {
    $(document).off("click", "button[operation='enable-service'][data-toggle!='modal']");
    $(document).on("click", "button[operation='enable-service'][data-toggle!='modal']", function(event) {
        var serviceName = $(event.currentTarget).attr("service-name");
        var protocol = $(event.currentTarget).attr("protocol");
        var host = $(event.currentTarget).attr("host");
        var process = $(event.currentTarget).attr("process");
        $.ajax({
            url: "/service/server/enable",
            type: "POST",
            data:"host="+host+":"+process+"&serviceName="+serviceName+"&protocol="+protocol,
            success: function() {
                showSuccessDialog();
                $("#services-status-overview-tbl").bootstrapTable("refresh");
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
                url: "/service/server/config/delete",
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
    $("#port").attr("value",data.port);
    $("#codec").attr("value", data.codec);
    $("#max-content-length").attr("value", data.maxContentLength);
    $("#use-native").attr("checked", data.useNative);
    $("#heartbeat-factory").attr("value", data.heartbeatFactory);
    $("#version").attr("value", data.version);
    $("#weight").attr("value", data.weight);
    $("#select-thread-size").attr("value", data.selectThreadSize);
    $("#max-server-connection").attr("value", data.maxServerConnection);
    $("#core-worker-thread").attr("value", data.coreWorkerThread);
    $("#max-worker-thread").attr("value", data.maxWorkerThread);
    $("#worker-queue-size").attr("value", data.workerQueueSize);
}
