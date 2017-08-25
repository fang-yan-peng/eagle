$(function() {
    renderRegCenterForDashboardNav();
    switchRegCenter();
    renderSkin();
    controlSubMenuStyle();
    refreshRegCenterNavTag();
});

function renderRegCenterForDashboardNav() {
    $.get("/registry-center/load", {}, function(data) {
        var index;
        var activatedRegCenterName;
        for (index = 0; index < data.length; index++) {
            if (data[index].activated) {
                activatedRegCenterName = data[index].name;
            }
        }
        var registryCenterDimension = $("#registry-center-dimension");
        registryCenterDimension.empty();
        for (index = 0; index < data.length; index++) {
            var regCenterName = data[index].name;
            var regCenterDisplayName;
            if (activatedRegCenterName && activatedRegCenterName === regCenterName) {
                regCenterDisplayName = "<b>" + regCenterName + "(<span data-lang='status-connected'></span>)</b>";
            } else {
                regCenterDisplayName = regCenterName;
            }
            registryCenterDimension.append("<li><a href='#' reg-name='" + regCenterName + "' data-loading-text='loading...'>" + regCenterDisplayName + "</a></li>");
            doLocale();
        }
        if (0 === data.length) {
            registryCenterDimension.hide();
        }
    });
    $(document).on("click", "#registry-center-dimension-link", function(event) {
        var $regCenterDimension = $("#registry-center-dimension");
        if ($regCenterDimension.children("li").length > 0) {
            $regCenterDimension.css("display", "");
        }
    });
}

function switchRegCenter() {
    $(document).on("click", "a[reg-name]", function(event) {
        var link = $(this).button("loading");
        var regCenterName = $(event.currentTarget).attr("reg-name");
        $.ajax({
            url: "/registry-center/connect",
            type: "POST",
            data: JSON.stringify({"name" : regCenterName}),
            contentType: "application/json",
            dataType: "json",
            success: function(data) {
                if (data) {
                    showSuccessDialog();
                    $("#reg-centers").bootstrapTable("refresh");
                    renderRegCenterForDashboardNav();
                    /*refreshJobNavTag();
                    refreshServerNavTag();*/
                    $("#content").load("/global/registry_center.html");
                    renderSidebarMenu($("#settings"));
                    $("#reg-center").parent().addClass("active");
                } else {
                    link.button("reset");
                    showRegCenterFailureDialog();
                }
            }
        });
    });
}


function renderSidebarMenu(div) {
    div.parent().children().removeClass("active");
    div.parent().children().children().children("li").removeClass("active");
    div.parent().children().children("ul").css("display","");
    div.addClass("active");
}

var my_skins = [
    "skin-blue",
    "skin-black",
    "skin-red",
    "skin-yellow",
    "skin-purple",
    "skin-green",
    "skin-blue-light",
    "skin-black-light",
    "skin-red-light",
    "skin-yellow-light",
    "skin-purple-light",
    "skin-green-light"
];

function renderSkin() {
    $("[data-skin]").on("click", function(event) {
        event.preventDefault();
        changeSkin($(this).data("skin"));
    });
}

function changeSkin(skinClass) {
    $.each(my_skins, function(index) {
        $("body").removeClass(my_skins[index]);
    });
    $("body").addClass(skinClass);
}

function controlSubMenuStyle() {
    $(".sub-menu").click(function() {
        $(this).parent().parent().children().removeClass("active");
        $(this).parent().addClass("active");
    });
}

function refreshRegCenterNavTag() {
    $.ajax({
        url: "/registry-center/load",
        cache: false,
        success: function(data) {
            $("#reg-nav-tag").text(data.length);
            if (data.length > 0) {
                for (var index = 0; index < data.length; index++) {
                    if (data[index].activated) {
                        refreshServiceNavTag();
                    } else {
                        $("#client-nav-tag").text("0");
                    }
                }
            } else {
                $("#client-nav-tag").text("0");
            }
        }
    });
}

function refreshServiceNavTag() {
    $.ajax({
        url: "/service/count",
        cache: false,
        success: function(data) {
            $("#client-nav-tag").text(data);
        }
    });
}

