function $initTable(options) {
    let table = layui.table;
    table.render({
        elem: '#' + options.tableId
        ,toolbar: options.toolbarId ? '#' + options.toolbarId : ''
        ,url: options.url
        ,method: options.method
        ,contentType: options.contentType
        ,cols: [options.cols]
        ,page: options.page
        ,parseData: function(res){
            return {
                "code": 0,
                "msg": '',
                "count": res.total,
                "data": res.records
            };
        }
    });
}

function $reload(options) {
    let table = layui.table;
    table.reloadData(options.tableId, {
        where: options.params,
        page: {curr: options.page, limit: options.limit} // 重新指向分页
    });
}