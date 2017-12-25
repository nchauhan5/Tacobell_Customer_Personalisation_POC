ACC.csvimport = {
    TEXT_CSV_CONTENT_TYPE: 'text/csv',
    APP_EXCEL_CONTENT_TYPE: 'application/vnd.ms-excel',

    _autoload: [
        ["changeFileUploadAppearance", $(".js-file-upload").length != 0],
        ["bindImportCSVActions", $(".js-import-csv").length != 0]
    ],

    changeFileUploadAppearance: function() {
        $('.js-file-upload__input').on('change',function () {
            var files = (this.files);
            var fileNames = "";

            for (var i = 0; i < files.length; i++) {
                fileNames += (files[i].name) + '<br/>';

            }

            $('.js-file-upload__file-name').unbind('mouseenter mouseleave');

            if (files.length > 1) {
                $('.js-file-upload__file-name').html(files.length + " files");
                $('.js-file-upload__file-name').hover(

                    function mouseIn() {
                        $(this).html(fileNames.toLowerCase());
                    }, function mouseOut() {

                        $(this).html(files.length + " files");
                    }
                );
            } else {
                $('.js-file-upload__file-name').html(fileNames.toLowerCase());
            }

            if($('.js-file-upload').parents('#cboxLoadedContent').length > 0){
                ACC.colorbox.resize();
            }
        })
    },

    bindImportCSVActions: function() {
        $('#chooseFileButton').on('click', function (event) {
            ACC.csvimport.clearGlobalAlerts();
        });

        $('#importButton').on('click', function (event) {
            event.preventDefault();
            ACC.csvimport.clearGlobalAlerts();

            if (!($('.js-file-upload__input').val().trim().length > 0)) {
                ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-no-file-chosen-error-message'});
                return;
            }

            var selectedFile = document.getElementById('csvFile').files[0];
            if (!ACC.csvimport.isSelectedFileValid(selectedFile)) {
                return;
            }

            var form = document.getElementById('importCSVSavedCartForm');
            var formData = new window.FormData(form);
            formData.append("csvFile", selectedFile);

            ACC.csvimport.displayGlobalAlert({type: 'warning', messageId: 'import-csv-upload-message'});
            ACC.csvimport.enableDisableActionButtons(false);

            $.ajax({
                url: form.action,
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false,
                success: function() {
                    ACC.csvimport.displayGlobalAlert({type: 'info', message: ''});
                    $('#import-csv-alerts .alert-info').append($('#import-csv-success-message').html());

                    ACC.csvimport.clearChosenFile();
                },
                error: function(jqXHR) {
                    if (jqXHR.status == 400) {
                        if (jqXHR.responseJSON) {
                            ACC.csvimport.displayGlobalAlert({type: 'error', message: jqXHR.responseJSON});
                            return;
                        }
                    }

                    ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-generic-error-message'});
                },
                complete: function() {
                    ACC.csvimport.enableDisableActionButtons(true);
                }
            });
        });
    },

    isSelectedFileValid: function(selectedFile) {
        if (window.File && window.Blob) {
            if (selectedFile) {
                if (!(selectedFile.type == ACC.csvimport.TEXT_CSV_CONTENT_TYPE ||
                    selectedFile.type == ACC.csvimport.APP_EXCEL_CONTENT_TYPE)) {
                    ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-file-csv-required'});
                    return false;
                }

                var fileName = selectedFile.name;
                if (!fileName || !(/\.csv$/i).test(fileName)) {
                    ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-file-csv-required'});
                    return false;
                }
            }

            var fileMaxSize = $('.js-file-upload__input').data('file-max-size');
            if ($.isNumeric(fileMaxSize) && selectedFile) {
                if (selectedFile.size > parseFloat(fileMaxSize)) {
                    ACC.csvimport.displayGlobalAlert({type: 'error', messageId: 'import-csv-file-max-size-exceeded-error-message'});
                    return false;
                }
            }
        }

        return true;
    },

    displayGlobalAlert: function(options) {
        ACC.csvimport.clearGlobalAlerts();
        var alertTemplateSelector;

        switch (options.type) {
            case 'error':
                alertTemplateSelector = '#global-alert-danger-template';
                break;
            case 'warning':
                alertTemplateSelector = '#global-alert-warning-template';
                break;
            default:
                alertTemplateSelector = '#global-alert-info-template';
        }

        if (typeof options.message != 'undefined') {
            $('#import-csv-alerts').append($(alertTemplateSelector).tmpl({message: options.message}));
        }

        if (typeof options.messageId != 'undefined')
        {
            $('#import-csv-alerts').append($(alertTemplateSelector).tmpl({message: $('#' + options.messageId).text()}));
        }
    },

    clearGlobalAlerts: function() {
        $('#import-csv-alerts').empty();
    },

    clearChosenFile: function() {
        document.getElementById('csvFile').value = '';
        $('.js-file-upload__file-name').text('');
    },

    enableDisableActionButtons: function(enable) {
        $('#chooseFileButton').attr('disabled', !enable);
        $('#importButton').prop('disabled', !enable);
    }
}