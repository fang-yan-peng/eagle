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
            var process = $("#process").val();
            var codec = $("#codec").val();
            var actives = $("#actives").val();
            var maxContentLength = $("#max-content-length").val();
            var activesWait = $("#actives-wait").val();
            var check = $("#check").prop("checked");
            var useNative = $("#use-native").prop("checked");
            var compress = $("#compress").prop("checked");
            var useDefault = $("#use-default").prop("checked");
            var cluster = $("#cluster").val();
            var heartbeatFactory = $("#heartbeat-factory").val();
            var heartbeat = $("#heartbeat").val();
            var version = $("#version").val();
            var statsLog = $("#stats-log").val();
            var retries = $("#retries").val();
            var callbackThread = $("#callback-thread").val();
            var callbackQueueSize = $("#callback-queue-size").val();
            var callbackWaitTime = $("#callback-wait-time").val();
            var requestTimeout = $("#request-timeout").val();
            var connectTimeout = $("#connect-timeout").val();
            var idleTime = $("#idle-time").val();
            var minClientConnection = $("#min-client-connection").val();
            var maxClientConnection= $("#max-client-connection").val();
            var maxInvokeError = $("#max-invoke-error").val();
            var loadbalance = $("#loadbalance").val();
            var haStrategy = $("#ha-strategy").val();
            var maxLifetime = $("#max-lifetime").val();
            var callback = $("#callback").val();
            var mock = $("#mock").val();
            var postJson = {protocol: protocol,
                serialization : serialization,
                group : group,
                serviceName: serviceName,
                host: host,
                process: process,
                codec: codec,
                actives: actives,
                maxContentLength: maxContentLength,
                activesWait: activesWait,
                check: check,
                useNative: useNative,
                compress: compress,
                useDefault: useDefault,
                cluster: cluster,
                heartbeatFactory: heartbeatFactory,
                heartbeat: heartbeat,
                version: version,
                statsLog: statsLog,
                retries: retries,
                callbackThread: callbackThread,
                callbackQueueSize: callbackQueueSize,
                callbackWaitTime: callbackWaitTime,
                requestTimeout: requestTimeout,
                connectTimeout: connectTimeout,
                idleTime: idleTime,
                minClientConnection: minClientConnection,
                maxClientConnection: maxClientConnection,
                maxInvokeError: maxInvokeError,
                loadbalance:loadbalance,
                haStrategy: haStrategy,
                maxLifetime: maxLifetime,
                callback: callback,
                mock: mock};
            submitAjax(postJson);
        }
    });
}

function submitAjax(postJson) {
    $.ajax({
        url: "/service/client/config/update",
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
            requestTimeout: {
                validators: {
                    regexp: {
                        regexp: /^(-?\d+)?$/,
                        message: $.i18n.prop("request-timeout-should-be-integer")
                    }
                }
            },
            connectTimeout: {
                validators: {
                    regexp: {
                        regexp: /^(-?\d+)?$/,
                        message: $.i18n.prop("connect-timeout-should-be-integer")
                    }
                }
            },
            idleTime: {
                validators: {
                    regexp: {
                        regexp: /^(-?\d+)?$/,
                        message: $.i18n.prop("idle-time-should-be-integer")
                    }
                }
            },
            minClientConnection: {
                validators: {
                    regexp: {
                        regexp: /^(-?\d+)?$/,
                        message: $.i18n.prop("min-client-connection-should-be-integer")
                    }
                }
            },
            maxClientConnection: {
                validators: {
                    regexp: {
                        regexp: /^(-?\d+)?$/,
                        message: $.i18n.prop("max-client-connection-should-be-integer")
                    }
                }
            },
            maxInvokeError: {
                validators: {
                    regexp: {
                        regexp: /^(-?\d+)?$/,
                        message: $.i18n.prop("max-invoke-error-should-be-integer")
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
