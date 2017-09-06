$(function() {
    tooltipLocale();
    validate();
    bindSubmitServiceSettingsForm();
    bindResetForm();
});

function tooltipLocale(){
    for (var i = 0; i < $("[data-toggle='tooltip']").length; i++) {
        var object = $("[data-toggle='tooltip']")[i];
        $(object).attr('title',$.i18n.prop("placeholder-" + object.getAttribute("id"))).tooltip('fixTitle');
    }
}

function bindSubmitServiceSettingsForm() {
    $("#update-service-info-btn").on("click", function(){
        var bootstrapValidator = $("#service-config-form").data("bootstrapValidator");
        bootstrapValidator.validate();
        if (bootstrapValidator.isValid()) {
            var protocol = $("#protocol").val();
            var serialization = $("#serialization").val();
            var group = $("#group").val();
            var serviceName = $("#service-name").val();
            var host = $("#host").val();
            var port = $("#port").val();
            var codec = $("#codec").val();
            var maxContentLength = $("#max-content-length").val();
            var useNative = $("#use-native").prop("checked");
            var heartbeatFactory = $("#heartbeat-factory").val();
            var version = $("#version").val();
            var weight = $("#weight").val();
            var selectThreadSize = $("#select-thread-size").val();
            var maxServerConnection = $("#max-server-connection").val();
            var coreWorkerThread = $("#core-worker-thread").val();
            var maxWorkerThread = $("#max-worker-thread").val();
            var workerQueueSize= $("#worker-queue-size").val();
            var protectStrategy= $("#protect-strategy").val();
            var postJson = {protocol: protocol,
                serialization : serialization,
                group : group,
                serviceName: serviceName,
                host: host,
                port: port,
                codec: codec,
                maxContentLength: maxContentLength,
                useNative: useNative,
                heartbeatFactory: heartbeatFactory,
                version: version,
                weight: weight,
                selectThreadSize: selectThreadSize,
                maxServerConnection: maxServerConnection,
                coreWorkerThread: coreWorkerThread,
                maxWorkerThread: maxWorkerThread,
                workerQueueSize: workerQueueSize,
                protectStrategy: protectStrategy};
            submitAjax(postJson);
        }
    });
}

function submitAjax(postJson) {
    $.ajax({
        url: "/service/server/config/update",
        type: "PUT",
        data: JSON.stringify(postJson),
        contentType: "application/json",
        dataType: "json",
        success: function() {
            $("#data-update-service").modal("hide");
            $("#service-status-overview-tbl").bootstrapTable("refresh");
            showSuccessDialog();
        }
    });
}

function validate() {
    $("#service-config-form").bootstrapValidator({
        message: "This value is not valid",
        feedbackIcons: {
            valid: "glyphicon glyphicon-ok",
            invalid: "glyphicon glyphicon-remove",
            validating: "glyphicon glyphicon-refresh"
        },
        fields: {
            weight: {
                validators: {
                    regexp: {
                        regexp: /^(-?\d+)?$/,
                        message: $.i18n.prop("weight-should-be-integer")
                    }
                }
            }
        }
    });
    $("#service-config-form").submit(function(event) {
        event.preventDefault();
    });
}

function bindResetForm() {
    $("#reset").click(function() {
        $("#service-config-form").data("bootstrapValidator").resetForm();
    });
}
