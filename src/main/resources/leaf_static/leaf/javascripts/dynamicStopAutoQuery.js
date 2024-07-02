function stopDymanicAutoQuery(layoutCode, tabCode, base_table) {
    window[layoutCode + '_' + tabCode + '_' + base_table + '_dsStopQuery'] = true;
    Leaf.onReady(function () {
        window[layoutCode + '_' + tabCode + '_' + base_table + '_dsStopQuery'] = false;
    })
}