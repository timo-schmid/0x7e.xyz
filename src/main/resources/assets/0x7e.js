
var showToast = function(message) {
    var toast = document.querySelector('#toast');
    toast.MaterialSnackbar.showSnackbar({ "message": message });
};

$(document).ready(function() {
    $('#submit').on('click', function() {
        var link = $('#link').val();
        $.post("/api/v1/shorten", JSON.stringify({"url": link}))
            .done(function(data) {
                var card = $(
                    '<div class="short-link-card mdl-card mdl-shadow--2dp">' +
                        '<div class="mdl-card__title">' +
                            '<h3>' +
                                '<a href="' + data.full + '">' + data.short + '</a>' +
                            '</h3>' +
                        '</div>' +
                        '<div class="mdl-card__supporting-text">' +
                            '<img src="' + data.qr + '" />' +
                        '</div>' +
                        '<div class="mdl-card__supporting-text">' +
                            '<a href="' + link + '">' + link + '</a>' +
                        '</div>' +
                    '</div>'
                );
                card.insertAfter($('#shortener'));
            })
            .fail(function(xhr) {
                if(xhr.responseJSON && xhr.responseJSON.message) {
                    showToast(xhr.responseJSON.message);
                } else {
                    showToast("Oops! Something went wrong.")
                }
            });
    })
});
