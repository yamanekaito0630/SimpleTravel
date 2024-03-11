$(document).on("click", "#addFavorite", function (e) {
    $.ajax({
        url: '/favorite/add',
        type: "POST",
        data: {
            houseId: $("#houseId").val(),
            _csrf: $("#csrf").val()
        }
    }).done(function (result) {
        if (result) {
            $("#addFavorite").remove();
            $("#favoriteElement").append(`<span id="removeFavorite" class="simpletravel-page-link">&#9829;お気に入り解除</span>`);
        } else {
            alert("処理を実行できませんでした。既に「お気に入り追加」されている可能性があります。問題が解決しない場合は管理者にお問合せください。");
        }
    }).fail(function () {
        alert("処理を実行できませんでした。管理者にお問合せください。");
    })
});

$(document).on("click", "#removeFavorite", function (e) {
    $.ajax({
        url: '/favorite/remove',
        type: "POST",
        data: {
            houseId: $("#houseId").val(),
            _csrf: $("#csrf").val()
        }
    }).done(function (result) {
        if (result) {
            $("#removeFavorite").remove();
            $("#favoriteElement").append(`<span id="addFavorite" class="simpletravel-page-link">&#9825;お気に入り追加</span>`);
        } else {
            alert("処理を実行できませんでした。既に「お気に入り解除」されている可能性があります。問題が解決しない場合は管理者にお問合せください。");
        }
    }).fail(function () {
        alert("処理を実行できませんでした。管理者にお問合せください。");
    })
});
