function fileSelect() {
    document.getElementById("fileToUpload").click();
}

$("#fileToUpload").change(function() {
    readFile(this, function (data) {
        $(".trial-img-upload").hide();
        $(".trial-img-change").show();
        $("#img_passport").attr("src",data);
        uploadImage(data);
    });
})

function readFile(input_file, get_data) {
    /*input_file：文件按钮对象*/
    /*get_data: 转换成功后执行的方法*/
    if (typeof (FileReader) === 'undefined') {
        toastr.warning("抱歉，你的浏览器不支持 FileReader，不能将图片转换为Base64，请使用现代浏览器操作！");
    } else {
        try {
            /*图片转Base64 核心代码*/
            var file = input_file.files[0];
            console.info(file);
            //这里我们判断下类型如果不是图片就返回 去掉就可以上传任意文件
            if (!/image\/\w+/.test(file.type)) {
                toastr.warning("请确保文件为图像类型!");
                return false;
            }
            var reader = new FileReader();
            reader.onload = function () {
                get_data(this.result);
            }
            reader.readAsDataURL(file);
        } catch (e) {
            toastr.warning('图片转Base64出错啦！' + e.toString());
        }
    }
}

function uploadImage(img) {
    $(".loading").show();
    $("#ocr_name").val("");
    $("#ocr_sex").val("");
    $("#ocr_code").val("");
    $("#ocr_id").val("");
    $("#ocr_birth").val("");
    $("#ocr_expiry").val("");
    $("#ocr_type").val("");
    $("#airline_code").val("");
    $("#ocr_format").val("");
    $.ajax({
        type: "POST",
        url: "passport/single_passport",
        data: {"imgFile":img.substr(img.indexOf(',') + 1)},
        async:true,
        success: function (message) {
            $(".loading").hide();
            if(message && !$.isEmptyObject(message)){
                $("#ocr_name").val(message.surname+"/"+message.givenname);
                $("#ocr_sex").val(message.sex);
                $("#ocr_code").val(message.country_code);
                $("#ocr_id").val(message.pid);
                $("#ocr_birth").val(message.birth_date);
                $("#ocr_expiry").val(message.expiry_date);
                $("#ocr_type").val(message.type);
            }else{
                toastr.options = {
                    progressBar: true,
                    positionClass: "toast-bottom-right",
                    showDuration: "300",
                    hideDuration: "1000",
                    timeOut: "2000",
                    extendedTimeOut: "1000",
                };
                toastr.error("未能识别护照信息!");
            }
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            toastr.error("未能识别护照信息!");
        }
    });
}

function save_ocr(){
    var ocr_name = $("#ocr_name").val();
    var ocr_sex = $("#ocr_sex").val();
    var ocr_code = $("#ocr_code").val();
    var ocr_id = $("#ocr_id").val();
    var ocr_birth = $("#ocr_birth").val();
    var ocr_expiry = $("#ocr_expiry").val();
    var ocr_type = $("#ocr_type").val();
    var airline_code = $("#airline_code").val();
    var ssr = "SSR DOCS ";
    ssr += airline_code;
    ssr += " HK1 ";
    ssr += ocr_type+"/"+ocr_code+"/"+ocr_id+"/"+ocr_code+"/"+ocr_birth+"/"+ocr_sex+"/"+ocr_expiry+"/"+ocr_name+"/"+ocr_type;
    $("#ocr_format").val(ssr);
    var passport = {};
    var name = ocr_name.split("/");
    passport.pid = ocr_id;
    passport.surname = name[0];
    passport.givenname = name[1];
    passport.sex = ocr_sex;
    passport.countryCode = ocr_code;
    passport.birthDate = ocr_birth;
    passport.type = ocr_type;
    passport.expiryDate = ocr_expiry;
    passport.status = 2;
    if(!$.isEmptyObject(passport)){
        $.post("passport/renewal",passport);
    }
}

document.addEventListener('paste', function(event) {
    var items = (event.clipboardData && event.clipboardData.items) || [];
    var file = null;
    if (items && items.length) {
        for (var i = 0; i < items.length; i++) {
            if (items[i].type.indexOf('image') !== -1) {
                file = items[i].getAsFile();
                break;
            }
        }
    }
    console.info(file);
    function readBlobAsDataURL(blob, callback) {
        var a = new FileReader();
        a.onload = function(e) {callback(this.result);};
        a.readAsDataURL(blob);
    }
    if(null!=file) {
        readBlobAsDataURL(file, function (data){
            $(".trial-img-upload").hide();
            $(".trial-img-change").show();
            $("#img_passport").attr("src",data);
            uploadImage(data)
        });
    }else{
        toastr.error("不能粘贴此图片");
    }

});
