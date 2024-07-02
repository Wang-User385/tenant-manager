(function(A) {
    var DOC = document,
        DOC_EL = DOC.documentElement,
        EACH = Ext.each,
        IS_EMPTY = Ext.isEmpty,
        FALSE = false,
        TRUE = true,
        NULL = null,
        CheckBox = A.CheckBox,
        _ = "_",
        __ = "__",
        _O = ".",
        _S = " ",
        _N = "",
        _K = "]",
        C = "-c",
        U = "-u",
        $L_copy = "-l-",
        $U = "-u-",
        ON = "on",
        PX = "px",
        TR = "tr",
        TD = "td",
        TH = "th",
        TB = "_tb",
        DIV = "div",
        TOP = "top",
        LEFT = "left",
        NONE = "none",
        WIDTH = "width",
        CENTER = "center",
        HIDDEN = "hidden",
        CURSOR = "cursor",
        VISIBLE = "",
        DEFAULT = "default",
        W_RESIZE = "w-resize",
        OUTLINE = "outline",
        OUTLINE_V = "1px dotted blue",
        ATYPE = "atype",
        APPEND = "append",
        INSERT_BEFORE = "insertBefore",
        INSERT_AFTER = "insertAfter",
        BEFORE = "before",
        NAVBAR = "_navbar",
        TEXT_OVERFLOW = "text-overflow",
        RECORD_ID = "recordid",
        DATA_INDEX = "dataindex",
        ROW_SELECT = "row-selected",
        REQUIRED = "required",
        ITEM_NOT_BLANK = "item-notBlank",
        ITEM_NOT_SPE_BLANK = "item-not-spe-blank",
        ITEM_INVALID = "item-invalid",
        ROW_ALT = "row-alt",
        GRID_ROWBOX = "grid-rowbox",
        $GRID_ROWBOX = _O + GRID_ROWBOX,
        GRID$ROWCHECK = "grid.rowcheck",
        GRID$ROWRADIO = "grid.rowradio",
        GRID$HEAD = "grid.head",
        GRID_CKB = "grid-ckb ",
        GRID_SELECT_ALL = "grid-select-all",
        MULTIPLE = "multiple",
        CHECKED_VALUE = "checkedvalue",
        READONLY = "-readonly",
        ITEM_CKB = "item-ckb",
        ITEM_CKB_SELF = ITEM_CKB + "-self",
        $ITEM_CKB_SELF = _O + ITEM_CKB_SELF,
        ITEM_CKB_U = ITEM_CKB + U,
        ITEM_CKB_C = ITEM_CKB + C,
        ITEM_CKB_READONLY_U = ITEM_CKB + READONLY + U,
        ITEM_CKB_READONLY_C = ITEM_CKB + READONLY + C,
        ITEM_RADIO_IMG = "item-radio-img",
        ITEM_RADIO_IMG_C = ITEM_RADIO_IMG + C,
        ITEM_RADIO_IMG_U = ITEM_RADIO_IMG + U,
        ITEM_RADIO_IMG_READONLY_C = ITEM_RADIO_IMG + READONLY + C,
        ITEM_RADIO_IMG_READONLY_U = ITEM_RADIO_IMG + READONLY + U,
        $ITEM_CKB_SELF_S$ITEM_CKB_C = $ITEM_CKB_SELF + _S + _O + ITEM_CKB_C,
        GRID_CELL = "grid-cell",
        CELL_EDITOR = "cell-editor",
        CELL_CHECK = "cellcheck",
        ROW_CHECK = "rowcheck",
        ROW_RADIO = "rowradio",
        ROW_NUMBER = "rownumber",
        GRID = "grid-",
        GRN_ROW_NUMBER = GRID + ROW_NUMBER,
        DESC = "desc",
        ASC = "asc",
        GRID_DESC = GRID + DESC,
        GRID_ASC = GRID + ASC,
        EVT_CLICK = "click",
        EVT_DBLCLICK = "dblclick",
        EVT_CELL_CLICK = "cellclick",
        EVT_RENDER = "render",
        EVT_ROW_CLICK = "rowclick",
        EVT_EDITOR_SHOW = "editorshow",
        EVT_NEXT_EDITOR_SHOW = "nexteditorshow",
        EVT_KEY_DOWN = "keydown",
        EVT_SELECT = "select",
        EVT_MOUSE_DOWN = "mousedown",
        EVT_MOUSE_MOVE = "mousemove",
        EVT_MOUSE_UP = "mouseup",
        EVT_CREATE_ROW = "createrow",
        EVT_ADD_ROW = "addrow",
        NOT_FOUND = "未找到",
        METHOD = "方法!",
        SELECT_TR_CLASS = "tr[class!=grid-hl]",
        SELECT_DIV_ATYPE = "div[atype=grid.headcheck]",
        SELECT_DATAINDEX = "[" + DATA_INDEX + "=",
        SELECT_TH_DATAINDEX = TH + SELECT_DATAINDEX,
        SELECT_TD_DATAINDEX = TD + SELECT_DATAINDEX,
        ROW_HEIGHT = 29,
        ROW_HEIGHT_TB = 36,
        ROW_HEIGHT_fb = 39,
        defaultColumnOptions = {
            autoadjust: TRUE,
            forexport: TRUE,
            hidden: FALSE,
            lock: FALSE,
            resizable: TRUE,
            rowspan: 1,
            sortable: TRUE,
            width: 100
        },
        findBorderParent = function(dom, isCheckBox) {
            return dom
        },
        sortByGroup = function(datas, colnames, value, _colname) {
            if (colnames.length) {
                var ret = [],
                    groups = [],
                    colname = colnames.shift();
                if (Ext.isDefined(value)) {
                    ret.value = value;
                    ret.colname = _colname
                }
                EACH(datas, function(r) {
                    var v = r.get(colname);
                    var g = findGroup(groups, v);
                    if (!g) {
                        groups.push(g = []);
                        if (IS_EMPTY(v)) {
                            g.value = namedEmptyGroup(r, colname)
                        } else {
                            g.value = v
                        }
                        g.colname = colname
                    }
                    g.push(r)
                });
                EACH(groups, function(group, index) {
                    ret.push(sortByGroup(group, [].concat(colnames), group.value, group.colname))
                });
                return ret
            } else {
                return datas
            }
        },
        concatGroups = function(groups) {
            var ret = [];
            EACH(groups, function(group) {
                if (Ext.isArray(group)) {
                    ret = ret.concat(concatGroups(group))
                } else {
                    ret.push(group)
                }
            });
            return ret
        },
        findGroup = function(groups, value) {
            var group = null;
            EACH(groups, function(g) {
                if (g.value == value) {
                    group = g;
                    return FALSE
                }
            });
            return group
        },
        searchGroup = function(groups, value) {
            var group = null;
            EACH(groups, function(g) {
                if (g.value == value || g.value && (g = searchGroup(g, value))) {
                    group = g;
                    return FALSE
                }
            });
            return group
        },
        searchRootGroupByRecord = function(groups, record) {
            var group = null;
            EACH(groups, function(g) {
                if (g.value ? searchRootGroupByRecord(g, record) : g === record) {
                    group = g;
                    return FALSE
                }
            });
            return group
        },
        removeFromGroup = function(groups, record, owner) {
            EACH(groups, function(g) {
                if (Ext.isArray(g)) {
                    removeFromGroup(g, record, groups)
                } else {
                    groups.remove(record);
                    return FALSE
                }
            });
            if (groups && !groups.length && owner) {
                owner.remove(groups)
            }
        },
        addIntoGroup = function() {
            function _find(groups, record, _colnames) {
                var find = FALSE,
                    name = _colnames.shift(),
                    value;
                if (!name) {
                    return record
                }
                value = record.get(name);
                if (IS_EMPTY(value)) {
                    value = namedEmptyGroup(record, name)
                } else {
                    EACH(groups, function(g) {
                        if (Ext.isArray(g)) {
                            if (value == g.value) {
                                find = g;
                                return FALSE
                            }
                        }
                    })
                }
                if (!find && name) {
                    find = [];
                    find.colname = name;
                    find.value = value
                }
                find.add(_find(find, record, _colnames));
                return find
            }
            return function(groups, record, cols) {
                var _colnames = [];
                Ext.each(cols, function(c) {
                    if (c.group) {
                        _colnames.push(c.name)
                    }
                });
                groups.add(_find(groups, record, _colnames));
                EACH(concatGroups(searchRootGroupByRecord(groups, record)), function(r) {
                    if (r !== record) {
                        var ds = r.ds;
                        ds[r.isSelected ? "select" : "unSelect"](record);
                        return FALSE
                    }
                })
            }
        }(),
        namedEmptyGroup = function(r, colname) {
            return "____empty__group__" + r.id + "__" + colname + "___"
        };
    A.Grid = Ext.extend(A.Component, {
        constructor: function(config) {
            var sf = this;
            sf.selectedId = NULL;
            sf.lockWidth = 0;
            sf.editorborder = TRUE;
            sf.autofocus = TRUE;
            A.Grid.superclass.constructor.call(sf, config);
            A.onReady(function() {
                sf.autofocus && sf.focus()
            })
        },
        initComponent: function(config) {
            A.Grid.superclass.initComponent.call(this, config);
            var sf = this,
                wp = sf.wrap,
                wb = sf.wb = Ext.get(sf.id + "_wrap");
            sf.fb = wb.child("div[atype=grid.fb]");
            if (sf.fb) {
                sf.uf = sf.fb.child("div[atype=grid.uf]")
            }
            sf.html = wp.parent("html");
            sf.uc = wp.child("div[atype=grid.uc]");
            sf.uh = wp.child("div[atype=grid.uh]");
            sf.ub = wp.child("div[atype=grid.ub]");
            sf.uht = wp.child("table[atype=grid.uht]");
            sf.lc = wp.child("div[atype=grid.lc]");
            sf.lh = wp.child("div[atype=grid.lh]");
            sf.lb = wp.child("div[atype=grid.lb]");
            sf.lht = wp.child("table[atype=grid.lht]");
            sf.sp = wp.child("div[atype=grid.spliter]");
            sf.sb = wp.child("span.sort-btn");
            Ext.getBody().insertFirst(sf.sp);
            sf.classfiyColumns();
            sf.initTemplate();
        },
        processListener: function(ou) {
            var sf = this;
            A.Grid.superclass.processListener.call(sf, ou);
            sf.wrap[ou](EVT_MOUSE_DOWN, sf.onMouseDown, sf);
            if (sf.canwheel !== FALSE) {
                sf.wb[ou]("mousewheel", sf.onMouseWheel, sf);
            }
            sf.wb[ou](Ext.isOpera ? "keypress" : EVT_KEY_DOWN, sf.handleKeyDown, sf)[ou]("keyup", sf.handleKeyUp, sf)[ou]("focus", sf.onFocus, sf)[ou]("blur", sf.onBlur, sf);
            sf.ub[ou]("scroll", sf.syncScroll, sf)[ou](EVT_CLICK, sf.onClick, sf)[ou]("focus", sf.onUbFocus, sf)[ou]("blur", sf.onUbBlur, sf)[ou](EVT_DBLCLICK, sf.onDblclick, sf);
            sf.uht[ou](EVT_MOUSE_MOVE, sf.onUnLockHeadMove, sf);
            sf.uh[ou](EVT_MOUSE_DOWN, sf.onHeadMouseDown, sf)[ou](EVT_CLICK, sf.onHeadClick, sf);
            if (sf.lb) {
                sf.lb[ou]("scroll", sf.syncScroll, sf)[ou](EVT_CLICK, sf.onClick, sf)[ou](EVT_DBLCLICK, sf.onDblclick, sf)
            }
            if (sf.lht) {
                sf.lht[ou](EVT_MOUSE_MOVE, sf.onLockHeadMove, sf)
            }
            if (sf.lh) {
                sf.lh[ou](EVT_MOUSE_DOWN, sf.onHeadMouseDown, sf)[ou](EVT_CLICK, sf.onHeadClick, sf)
            }
            sf[ou](EVT_CELL_CLICK, sf.onCellClick, sf);
            sf.wrap[ou]("DOMSubtreeModified", sf.bodyUpdate, sf);
            sf.html[ou]("DOMSubtreeModified", sf.htmlUpdate, sf);
            sf.wrap[ou]("click", sf.gridClick, sf);
            if (!Ext.isIE8) {
                sf.ub[ou]("mousewheel", sf.onMouseWheel, sf);
                /*$jq("#"+sf.ub.id).niceScroll({
                    nativeparentscrolling: true
                });*/
            }
        },
        initEvents: function() {
            A.Grid.superclass.initEvents.call(this);
            this.addEvents(EVT_RENDER, EVT_KEY_DOWN, EVT_DBLCLICK, EVT_CELL_CLICK, EVT_ROW_CLICK, EVT_EDITOR_SHOW, EVT_NEXT_EDITOR_SHOW, EVT_CREATE_ROW)
        },
        htmlUpdate: function() {
            if (!Ext.isIE8) {
                $jq("#"+this.html.id).getNiceScroll().resize();
            }
        },
        bodyUpdate: function() {
            var sf = this;
            if (!Ext.isIE8) {
                $jq("#" + sf.ub.id).getNiceScroll().resize();
            }
        },
        gridClick: function(e, t) {
            var sf = this,
                target = Ext.get(t);
            if (target.dom.className == "sort-btn") {
                sf.sortPosition(target)
            } else {
                if (sf.st) {
                    sf.st.remove()
                }
            }
        },
        sortPosition: function(ele, t) {
            var sf = this,
                screenWidth = $L.getViewportWidth() - 20,
                screenHeight = $L.getViewportHeight(),
                eleH = ele.getHeight(),
                eleW = ele.getWidth(),
                eleX = ele.getX(),
                eleY = ele.getY(),
                x = eleX,
                y = eleY + eleH;
            body = Ext.getBody(), sortWrap = body.child(".sort-wrap");
            if (sortWrap) {
                var stLeft = sortWrap.getLeft();
                var stTop = sortWrap.getTop();
                var stH = sortWrap.getHeight();
                var stW = sortWrap.getWidth();
                if ((x == stLeft) || ((x == stLeft + stW + eleX - screenWidth) && eleX + stW > screenWidth)) {
                    sortWrap.moveTo("-10000", "-10000")
                } else {
                    sortWrap.remove();
                    sf.creatListView(ele);
                    var stHeight = sf.st.getHeight();
                    var stWidth = sf.st.getWidth();
                    if (screenHeight > stHeight + y && screenWidth > stWidth + x) {
                        sf.st.addClass("animated zoomInDownPopup");
                        setTimeout(function() {
                            sf.st.removeClass("animated zoomInDownPopup")
                        }, 600);
                        sf.st.moveTo(x, y)
                    } else {
                        if (screenHeight < stHeight + y && screenWidth > stWidth + x) {
                            y = y - eleH - 10;
                            sf.st.addClass("animated zoomInUpPopup");
                            setTimeout(function() {
                                sf.st.removeClass("animated zoomInUpPopup")
                            }, 600);
                            sf.st.moveTo(x, y)
                        } else {
                            if (screenHeight > stHeight + y && screenWidth < stWidth + x) {
                                x = screenWidth - stWidth;
                                sf.st.addClass("animated zoomInDownPopup");
                                setTimeout(function() {
                                    sf.st.removeClass("animated zoomInDownPopup")
                                }, 600);
                                sf.st.moveTo(x, y)
                            } else {
                                x = screenWidth - stWidth;
                                y = y - eleH - 10;
                                sf.st.addClass("animated zoomInUpPopup");
                                setTimeout(function() {
                                    sf.st.removeClass("animated zoomInUpPopup")
                                }, 600);
                                sf.st.moveTo(x, y)
                            }
                        }
                    }
                }
            } else {
                sf.creatListView(ele);
                var stHeight = sf.st.getHeight();
                var stWidth = sf.st.getWidth();
                if (screenHeight > stHeight + y && screenWidth > stWidth + x) {
                    sf.st.addClass("animated zoomInDownPopup");
                    setTimeout(function() {
                        sf.st.removeClass("animated zoomInDownPopup")
                    }, 600);
                    sf.st.moveTo(x, y)
                } else {
                    if (screenHeight < stHeight + y && screenWidth > stWidth + x) {
                        y = y - eleH - 10;
                        sf.st.addClass("animated zoomInUpPopup");
                        setTimeout(function() {
                            sf.st.removeClass("animated zoomInUpPopup")
                        }, 600);
                        sf.st.moveTo(x, y)
                    } else {
                        if (screenHeight > stHeight + y && screenWidth < stWidth + x) {
                            x = screenWidth - stWidth;
                            sf.st.addClass("animated zoomInDownPopup");
                            setTimeout(function() {
                                sf.st.removeClass("animated zoomInDownPopup")
                            }, 600);
                            sf.st.moveTo(x, y)
                        } else {
                            x = screenWidth - stWidth;
                            y = y - eleH - 10;
                            sf.st.addClass("animated zoomInUpPopup");
                            setTimeout(function() {
                                sf.st.removeClass("animated zoomInUpPopup")
                            }, 600);
                            sf.st.moveTo(x, y)
                        }
                    }
                }
            }
        },
        creatListView: function(target) {
            var sf = this;
            var ds = sf.dataset;
            Ext.onReady(function() {
                var list = ["<div class='list-wrap'><ul class='list-box'>"],
                    columns = sf.columns;
                var records = sf.dataset.data;
                for (var i = 0; i < columns.length; i++) {
                    if (columns[i].type != "rowcheck") {
                        list.push('<li tabIndex="', i, '"><div class="item-ckb item-ckb-', columns[i].hidden == true ? "u" : "c", '"></div><span>' + columns[i].prompt + "</span></li>")
                    }
                }
                list.push("</ul></div>");
                var filter = ["<div class='filter-wrap'>"];
                filter.push('<div class="filter-title">显示符合以下条件的行</div>' + '<div class="first-select"><select class="select1">' + '<option value="大于">大于</option><option value="大于等于">大于等于</option><option value="小于">小于</option><option value="小于等于">小于等于</option><option value="等于">等于</option><option value="不等于">不等于</option><option value="开头为">开头为</option>' + '<option value="包含">包含</option><option value="不包含">不包含</option><option value="结尾为">结尾为</option><option value="is null">is null</option>' + '<option value="is not null">is not null</option><option value="is empty">is empty</option><option value="is not empty">is not empty</option>' + '</select></div><input type="text" class="input-value first-input"/>' + '<div class="first-select second-select"><select class="select2"><option value="并且">并且</option><option value="或者">或者</option></select></div>' + '<div class="first-select last-select"><select class="select3">' + '<option value="大于">大于</option><option value="大于等于">大于等于</option><option value="小于">小于</option><option value="小于等于">小于等于</option><option value="等于">等于</option><option value="不等于">不等于</option><option value="开头为">开头为</option>' + '<option value="包含">包含</option><option value="不包含">不包含</option><option value="结尾为">结尾为</option><option value="is null">is null</option>' + '<option value="is not null">is not null</option><option value="is empty">is empty</option><option value="is not empty">is not empty</option>' + '</select></div><input type="text" class="input-value second-input"/>' + '<div class="btn-wrap"><button class="filter-btn" value="过滤">过滤</button><button class="clear-btn" value="清除">清除</button></div>');
                sf.st = new Ext.Template("<div class='sort-wrap'><ul class='sort-box'><li class='ascending'><span class='ascend-icon'></span><span>升序</span></li>" + "<li class='descending'><span class='descend-icon'></span><span>降序</span></li>" + "<li class='column' style='position:relative;border-top:1px solid #ccc;'><span class='list-icon'></span><span>列</span><span class='list-expend'></span> " + list.join("") + "</li>" + "<li class='filter' style='position:relative;border-top:1px solid #ccc;'><span class='filter-icon'></span><span>过滤</span><span class='filter-expend'></span> " + filter.join("") + "</li>" + "</ul></div>").insertFirst(document.body, {
                    shadow: Ext.isIE ? '<div class="item-ie-shadow"></div>' : ""
                }, true)
            });
            sf.sortDesc(sf.st, target)
        },
        sortDesc: function(el, target) {
            var column = el.child(".column");
            var filter = el.child(".filter");
            var listBox = el.child("ul.list-box");
            var filterBox = el.child("div.filter-wrap");
            var filterBtn = el.child("button.filter-btn");
            var clearBtn = el.child("button.clear-btn");
            var ascending = el.child(".ascending");
            var descending = el.child(".descending");
            column.on("mouseenter", this.onColumnShow, this);
            column.on("mouseleave", this.onColumnHide, this, el);
            filter.on("mouseenter", this.onColumnShow, this);
            filter.on("click", this.onColumnShow, this);
            filter.on("mouseleave", this.onColumnHide, this, el);
            listBox.on("click", this.onViewClick, this, target);
            filterBox.on("click", this.onfilterBoxClick, this, target);
            filterBtn.on("click", this.onfilterBtnClick, this, target);
            clearBtn.on("click", this.onclearBtnClick, this, target);
            ascending.on("click", this.onAscendingClick, this, target);
            descending.on("click", this.onDescendingClick, this, target)
        },
        onclearBtnClick: function() {
            var sf = this,
                ds = sf.dataset;
            sf.bind(ds.id);
            sf.st.remove()
        },
        onfilterBtnClick: function(e, t, target) {
            var sf = this;
            isFilter = false, td = target.parent("td"), tdDisplay = td.getAttribute("dataindex"), ds = sf.dataset, datas = ds.data, selectOne = sf.st.child(".select1"), selectTwo = sf.st.child(".select2"), selectThree = sf.st.child(".select3"), inputOne = sf.st.child(".first-input"), inputTwo = sf.st.child(".second-input"), inputValue1 = inputOne.getValue(), inputValue2 = inputTwo.getValue(), selectValue1 = selectOne.getValue(), selectValue2 = selectTwo.getValue(), selectValue3 = selectThree.getValue();
            if (inputValue1 == "" && inputValue2 != "") {
                sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay);
                sf.onLoad()
            } else {
                if (inputValue1 != "" && inputValue2 == "") {
                    sf.filterOperation(selectValue1, datas, inputValue1, tdDisplay);
                    sf.onLoad()
                } else {
                    if (inputValue1 != "" && inputValue2 != "") {
                        if (selectValue2 === "或者") {
                            for (var i = 0; i < datas.length;) {
                                if (selectValue1 === "开头为") {
                                    var inpL1 = inputValue1.length;
                                    if (!(datas[i].data[tdDisplay].slice(0, inpL1) == inputValue1 || sf.orFilter(datas, i, tdDisplay, inputValue2))) {
                                        datas.remove(datas[i])
                                    } else {
                                        i++
                                    }
                                } else {
                                    if (selectValue1 === "等于") {
                                        var inpL1 = inputValue1.length;
                                        if (!(datas[i].data[tdDisplay] === inputValue1 || sf.orFilter(datas, i, tdDisplay, inputValue2))) {
                                            datas.remove(datas[i])
                                        } else {
                                            i++
                                        }
                                    } else {
                                        if (selectValue1 === "不等于") {
                                            var inpL1 = inputValue1.length;
                                            if (!(datas[i].data[tdDisplay] !== inputValue1 || sf.orFilter(datas, i, tdDisplay, inputValue2))) {
                                                datas.remove(datas[i])
                                            } else {
                                                i++
                                            }
                                        } else {
                                            if (selectValue1 === "结尾为") {
                                                var inpL1 = inputValue1.length;
                                                if (!(datas[i].data[tdDisplay].substring(datas[i].data[tdDisplay].length - inpL1) === inputValue1 || sf.orFilter(datas, i, tdDisplay, inputValue2))) {
                                                    datas.remove(datas[i])
                                                } else {
                                                    i++
                                                }
                                            } else {
                                                if (selectValue1 === "包含") {
                                                    var inpL1 = inputValue1.length;
                                                    if (!(datas[i].data[tdDisplay].indexOf(inputValue1) != -1 || sf.orFilter(datas, i, tdDisplay, inputValue2))) {
                                                        datas.remove(datas[i])
                                                    } else {
                                                        i++
                                                    }
                                                } else {
                                                    if (selectValue1 === "不包含") {
                                                        var inpL1 = inputValue1.length;
                                                        if (!(!(datas[i].data[tdDisplay].indexOf(inputValue1) != -1) || sf.orFilter(datas, i, tdDisplay, inputValue2))) {
                                                            datas.remove(datas[i])
                                                        } else {
                                                            i++
                                                        }
                                                    }else{
                                                        var curValue = datas[i].data[tdDisplay];
                                                        var numFlag = !!parseFloat(curValue) && !!parseFloat(inputValue1);
                                                        var orRightFlag = sf.orFilter(datas, i, tdDisplay, inputValue2);
                                                        if(selectValue1 === "大于"){
                                                            if( ((numFlag && (parseFloat(curValue) > parseFloat(inputValue1))) || (!numFlag && (curValue > (inputValue1)))) || (orRightFlag) ){
                                                                i++
                                                            } else {
                                                                datas.remove(datas[i])
                                                            }
                                                        }else if(selectValue1 === "大于等于"){
                                                            if( ((numFlag && (parseFloat(curValue) >= parseFloat(inputValue1))) || (!numFlag && (curValue >= (inputValue1)))) || (orRightFlag) ){
                                                                i++
                                                            } else {
                                                                datas.remove(datas[i])
                                                            }
                                                        }else if(selectValue1 === "小于"){
                                                            if( ((numFlag && (parseFloat(curValue) < parseFloat(inputValue1))) || (!numFlag && (curValue < (inputValue1)))) || (orRightFlag) ){
                                                                i++
                                                            } else {
                                                                datas.remove(datas[i])
                                                            }
                                                        }else if(selectValue1 === "小于等于"){
                                                            if( ((numFlag && (parseFloat(curValue) <= parseFloat(inputValue1))) || (!numFlag && (curValue <= (inputValue1)))) || (orRightFlag) ){
                                                                i++
                                                            } else {
                                                                datas.remove(datas[i])
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            sf.onLoad()
                        } else {
                            for (var i = 0; i < datas.length;) {
                                if (selectValue1 === "开头为") {
                                    var inpL = inputValue1.length;
                                    for (var i = 0; i < datas.length;) {
                                        if (!(datas[i].data[tdDisplay].slice(0, inpL) == inputValue1)) {
                                            datas.remove(datas[i])
                                        } else {
                                            i++
                                        }
                                    }
                                    sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                } else {
                                    if (selectValue1 === "包含") {
                                        var inpL = inputValue1.length;
                                        for (var i = 0; i < datas.length;) {
                                            if (!(datas[i].data[tdDisplay].indexOf(inputValue1) != -1)) {
                                                datas.remove(datas[i])
                                            } else {
                                                i++
                                            }
                                        }
                                        sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                    } else {
                                        if (selectValue1 === "不包含") {
                                            var inpL = inputValue1.length;
                                            for (var i = 0; i < datas.length;) {
                                                if (datas[i].data[tdDisplay].indexOf(inputValue1) != -1) {
                                                    datas.remove(datas[i])
                                                } else {
                                                    i++
                                                }
                                            }
                                            sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                        } else {
                                            if (selectValue1 === "结尾为") {
                                                var inpL = inputValue1.length;
                                                for (var i = 0; i < datas.length;) {
                                                    if (!(datas[i].data[tdDisplay].substring(datas[i].data[tdDisplay].length - inpL1) === inputValue1)) {
                                                        datas.remove(datas[i])
                                                    } else {
                                                        i++
                                                    }
                                                }
                                                sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                            } else {
                                                if (selectValue1 === "不等于") {
                                                    var inpL = inputValue1.length;
                                                    for (var i = 0; i < datas.length;) {
                                                        if (!(datas[i].data[tdDisplay] !== inputValue1)) {
                                                            datas.remove(datas[i])
                                                        } else {
                                                            i++
                                                        }
                                                    }
                                                    sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                                }else{
                                                    var curValue = datas[i].data[tdDisplay];
                                                    var numFlag = !!parseFloat(curValue) && !!parseFloat(inputValue1);

                                                    if (selectValue1 === "大于") {
                                                        for (var i = 0; i < datas.length;) {
                                                            if ( (numFlag && (parseFloat(curValue) > parseFloat(inputValue1))) || (!numFlag && curValue > inputValue1) ) {
                                                                datas.remove(datas[i])
                                                            } else {
                                                                i++
                                                            }
                                                        }
                                                        sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                                    }else if (selectValue1 === "大于等于") {
                                                        for (var i = 0; i < datas.length;) {
                                                            if ( (numFlag && (parseFloat(curValue) >= parseFloat(inputValue1))) || (!numFlag && curValue >= inputValue1) ) {
                                                                i++
                                                            } else {
                                                                datas.remove(datas[i])
                                                            }
                                                        }
                                                        sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                                    }else if (selectValue1 === "小于") {
                                                        for (var i = 0; i < datas.length;) {
                                                            if ( (numFlag && (parseFloat(curValue) < parseFloat(inputValue1))) || (!numFlag && curValue < inputValue1) ) {
                                                                i++
                                                            } else {
                                                                datas.remove(datas[i])
                                                            }
                                                        }
                                                        sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                                    }else if (selectValue1 === "小于等于") {
                                                        for (var i = 0; i < datas.length;) {
                                                            if ( (numFlag && (parseFloat(curValue)  <= parseFloat(inputValue1))) || (!numFlag && curValue <= inputValue1) ) {
                                                                i++
                                                            } else {
                                                                datas.remove(datas[i])
                                                            }
                                                        }
                                                        sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                                    }else if (selectValue1 === "等于") {
                                                        for (var i = 0; i < datas.length;) {
                                                            if ( curValue == inputValue1) {
                                                                i++
                                                            } else {
                                                                datas.remove(datas[i])
                                                            }
                                                        }
                                                        sf.filterOperation(selectValue3, datas, inputValue2, tdDisplay)
                                                    }else{
                                                        i++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            sf.onLoad()
                        }
                    } else {
                        for (var i = 0; i < datas.length;) {
                            if (selectValue1 === "is null" || selectValue3 === "is null") {
                                if (datas[i].data[tdDisplay] != null) {
                                    datas.remove(datas[i])
                                } else {
                                    i++
                                }
                            } else {
                                if (selectValue1 === "is not null" || selectValue3 === "is not null") {
                                    if (datas[i].data[tdDisplay] == null) {
                                        datas.remove(datas[i])
                                    } else {
                                        i++
                                    }
                                } else {
                                    if (selectValue1 === "is empty" || selectValue3 === "is empty") {
                                        if (datas[i].data[tdDisplay] !== "") {
                                            datas.remove(datas[i])
                                        } else {
                                            i++
                                        }
                                    } else {
                                        if (selectValue1 === "is not empty" || selectValue3 === "is not empty") {
                                            if (datas[i].data[tdDisplay] === "") {
                                                datas.remove(datas[i])
                                            } else {
                                                i++
                                            }
                                        } else {
                                            i++
                                        }
                                    }
                                }
                            }
                        }
                        sf.onLoad()
                    }
                }
            }
            sf.st.remove()
        },
        orFilter: function(datas, i, tdDisplay, inputValue2) {
            var inpL2 = inputValue2.length;
            if (selectValue3 === "结尾为") {
                if (datas[i].data[tdDisplay].substring(datas[i].data[tdDisplay].length - inpL2) === inputValue2) {
                    return true
                } else {
                    return false
                }
            } else {
                if (selectValue3 === "开头为") {
                    var inpL2 = inputValue2.length;
                    if (datas[i].data[tdDisplay].slice(0, inpL2) == inputValue2) {
                        return true
                    } else {
                        return false
                    }
                } else {
                    if (selectValue3 === "等于") {
                        if (datas[i].data[tdDisplay] === inputValue2) {
                            return true
                        } else {
                            return false
                        }
                    } else {
                        if (selectValue3 === "不等于") {
                            if (datas[i].data[tdDisplay] !== inputValue2) {
                                return true
                            } else {
                                return false
                            }
                        } else {
                            if (selectValue3 === "包含") {
                                if (datas[i].data[tdDisplay].indexOf(inputValue2) != -1) {
                                    return true
                                } else {
                                    return false
                                }
                            } else {
                                if (selectValue3 === "不包含") {
                                    if (!(datas[i].data[tdDisplay].indexOf(inputValue2) != -1)) {
                                        return true
                                    } else {
                                        return false
                                    }
                                }else{
                                    var curValue = datas[i].data[tdDisplay];
                                    var numFlag = !!parseFloat(curValue) && !!parseFloat(inputValue2);

                                    if (selectValue3 === "大于") {
                                        if ((numFlag && parseFloat(curValue) > parseFloat(inputValue2)) || (!numFlag && curValue > inputValue2)) {
                                            return true
                                        } else {
                                            return false
                                        }
                                    }else if (selectValue3 === "大于等于") {
                                        if ((numFlag && parseFloat(curValue) >= parseFloat(inputValue2)) || (!numFlag && curValue >= inputValue2)) {
                                            return true
                                        } else {
                                            return false
                                        }
                                    }else if (selectValue3 === "小于") {
                                        if ((numFlag && parseFloat(curValue) < parseFloat(inputValue2)) || (!numFlag && curValue < inputValue2)) {
                                            return true
                                        } else {
                                            return false
                                        }
                                    }else if (selectValue3 === "小于等于") {
                                        if ((numFlag && parseFloat(curValue) <= parseFloat(inputValue2)) || (!numFlag && curValue <= inputValue2)) {
                                            return true
                                        } else {
                                            return false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        filterOperation: function(stValue, datas, iptValue, tdp) {
            var sf = this;
            var ds = sf.dataset;
            var isFilter = false;
            for (var i = 0; i < datas.length;) {
                if (stValue === "等于") {
                    if (datas[i].data[tdp] != iptValue) {
                        datas.remove(datas[i])
                    } else {
                        i++
                    }
                } else {
                    if (stValue === "不等于") {
                        if (datas[i].data[tdp] == iptValue) {
                            datas.remove(datas[i])
                        } else {
                            i++
                        }
                    } else {
                        if (stValue === "开头为") {
                            var inpL = iptValue.length;
                            if (datas[i].data[tdp].slice(0, inpL) !== iptValue) {
                                datas.remove(datas[i])
                            } else {
                                i++
                            }
                        } else {
                            if (stValue === "包含") {
                                if (!(datas[i].data[tdp].indexOf(iptValue) != -1)) {
                                    datas.remove(datas[i])
                                } else {
                                    i++
                                }
                            } else {
                                if (stValue === "不包含") {
                                    if (datas[i].data[tdp].indexOf(iptValue) != -1) {
                                        datas.remove(datas[i])
                                    } else {
                                        i++
                                    }
                                } else {
                                    if (stValue === "结尾为") {
                                        var inpL = iptValue.length;
                                        if (datas[i].data[tdp].substring(datas[i].data[tdp].length - inpL) !== iptValue) {
                                            datas.remove(datas[i])
                                        } else {
                                            i++
                                        }
                                    }else{
                                        var curValue = datas[i].data[tdp];
                                        var numFlag = !!parseFloat(curValue) && !!parseFloat(iptValue);

                                        if (stValue === "大于") {
                                            if ( (numFlag && parseFloat(curValue) > parseFloat(iptValue)) || (!numFlag && curValue > iptValue)) {
                                                i++
                                            } else {
                                                datas.remove(datas[i])
                                            }
                                        }else if (stValue === "大于等于") {
                                            if ( (numFlag && parseFloat(curValue) >= parseFloat(iptValue)) || (!numFlag && curValue >= iptValue)) {
                                                i++
                                            } else {
                                                datas.remove(datas[i])
                                            }
                                        }else if (stValue === "小于") {
                                            if ( (numFlag && parseFloat(curValue) < parseFloat(iptValue)) || (!numFlag && curValue < iptValue)) {
                                                i++
                                            } else {
                                                datas.remove(datas[i])
                                            }
                                        }else if (stValue === "小于等于") {
                                            if ( (numFlag && parseFloat(curValue) <= parseFloat(iptValue)) || (!numFlag && curValue <= iptValue)) {
                                                i++
                                            } else {
                                                datas.remove(datas[i])
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        onfilterBoxClick: function(e) {
            e.stopEvent()
        },
        onAscendingClick: function(e, t, target) {
            target = Ext.fly(target).parent(TD);
            var sf = this,
                ds = sf.dataset;
            var index = target.getAttributeNS(_N, DATA_INDEX),
                col = sf.findColByName(index);
            if (ds.isModified() && ds.sorttype == "remote") {
                A.showInfoMessage("提示", "有未保存数据!");
                return
            }
            ds.sort(index, "ASC");
            sf.st.moveTo("-10000", "-10000")
        },
        onDescendingClick: function(e, t, target) {
            target = Ext.fly(target).parent(TD);
            var sf = this,
                ds = sf.dataset;
            var index = target.getAttributeNS(_N, DATA_INDEX),
                col = sf.findColByName(index);
            if (ds.isModified() && ds.sorttype == "remote") {
                A.showInfoMessage("提示", "有未保存数据!");
                return
            }
            ds.sort(index, "DESC");
            sf.st.moveTo("-10000", "-10000")
        },
        onViewClick: function(e, t, target) {
            e.stopEvent();
            var sf = this,
                columns = sf.columns;
            var etg = Ext.get(e.target);
            var n = 0;
            for (var i = 0; i < columns.length; i++) {
                if (columns[i].type != "rowcheck") {
                    if (columns[i].forexport == true) {
                        n = n + 1
                    }
                }
            }
            if (etg.dom.nodeName == "LI") {
                var index = etg.getAttribute("tabIndex");
                if (etg.child("div.item-ckb").hasClass("item-ckb-c")) {
                    if (n > 1) {
                        etg.child("div.item-ckb").removeClass("item-ckb-c").addClass("item-ckb-u");
                        sf.hideColumn(columns[index].name)
                    }
                } else {
                    etg.child("div.item-ckb").removeClass("item-ckb-u").addClass("item-ckb-c");
                    sf.showColumn(columns[index].name)
                }
            } else {
                var etgP = etg.parent("li");
                var index = etgP.getAttribute("tabIndex");
                if (etgP.child("div.item-ckb").hasClass("item-ckb-c")) {
                    if (n > 1) {
                        etgP.child("div.item-ckb").removeClass("item-ckb-c").addClass("item-ckb-u");
                        sf.hideColumn(columns[index].name)
                    }
                } else {
                    etgP.child("div.item-ckb").removeClass("item-ckb-u").addClass("item-ckb-c");
                    sf.showColumn(columns[index].name)
                }
            }
        },
        columnPosition: function(sf, wrap) {
            var listW = wrap.getWidth(),
                listH = wrap.getHeight(),
                screenWidth = $L.getViewportWidth() - 20,
                screenHeight = $L.getViewportHeight(),
                columnH = sf.getHeight(),
                columnW = sf.getWidth(),
                columnX = sf.getX(),
                columnY = sf.getY(),
                x = columnW + columnX,
                y = columnY;
            if (screenHeight > y + listH && screenWidth > x + listW) {
                wrap.addClass("animated zoomInDownPopup");
                setTimeout(function() {
                    wrap.removeClass("animated zoomInDownPopup")
                }, 600);
                wrap.moveTo(x, y)
            } else {
                if (screenHeight < y + listH && screenWidth > x + listW) {
                    y = y + columnH / 2;
                    wrap.addClass("animated zoomInUpPopup");
                    setTimeout(function() {
                        wrap.removeClass("animated zoomInUpPopup")
                    }, 600);
                    wrap.moveTo(x, y)
                } else {
                    if (screenHeight > y + listH && screenWidth < x + listW) {
                        x = columnX - listW;
                        wrap.addClass("animated zoomInDownPopup");
                        setTimeout(function() {
                            wrap.removeClass("animated zoomInDownPopup")
                        }, 600);
                        wrap.moveTo(x, y)
                    } else {
                        x = columnX - listW;
                        y = y + columnH / 2;
                        wrap.addClass("animated zoomInUpPopup");
                        setTimeout(function() {
                            wrap.removeClass("animated zoomInUpPopup")
                        }, 600);
                        wrap.moveTo(x, y)
                    }
                }
            }
        },
        onColumnShow: function(e, el) {
            e.stopEvent();
            var sf = Ext.get(el);
            var listWrap = sf.child(".list-wrap");
            var filterWrap = sf.child(".filter-wrap");
            if (listWrap) {
                this.columnPosition(sf, listWrap)
            }
            if (filterWrap) {
                this.columnPosition(sf, filterWrap)
            }
        },
        onColumnHide: function(e, t, el) {
            e.stopEvent();
            var listWrap = el.child(".list-wrap");
            if (listWrap) {
                listWrap.moveTo("-10000", "-10000")
            } else {
                if (sf.hasClass("list-wrap")) {
                    listWrap.moveTo("-10000", "-10000")
                }
            }
            var filterWrap = el.child(".filter-wrap");
            if (filterWrap) {
                if(!$jq(e.target).parents().hasClass("first-select")) {
                    filterWrap.moveTo("-10000", "-10000")
                }
            } else {
                if (sf.hasClass("filter-wrap")) {
                    filterWrap.moveTo("-10000", "-10000")
                }
            }
        },
        sortPosition: function(ele, t) {
            var sf = this,
                screenWidth = $L.getViewportWidth() - 20,
                screenHeight = $L.getViewportHeight(),
                eleH = ele.getHeight(),
                eleW = ele.getWidth(),
                eleX = ele.getX(),
                eleY = ele.getY(),
                x = eleX,
                y = eleY + eleH;
            body = Ext.getBody(), sortWrap = body.child(".sort-wrap");
            if (sortWrap) {
                var stLeft = sortWrap.getLeft();
                var stTop = sortWrap.getTop();
                var stH = sortWrap.getHeight();
                var stW = sortWrap.getWidth();
                if ((x == stLeft) || ((x == stLeft + stW + eleX - screenWidth) && eleX + stW > screenWidth)) {
                    sortWrap.moveTo("-10000", "-10000")
                } else {
                    sortWrap.remove();
                    sf.creatListView(ele);
                    var stHeight = sf.st.getHeight();
                    var stWidth = sf.st.getWidth();
                    if (screenHeight > stHeight + y && screenWidth > stWidth + x) {
                        sf.st.addClass("animated zoomInDownPopup");
                        setTimeout(function() {
                            sf.st.removeClass("animated zoomInDownPopup")
                        }, 600);
                        sf.st.moveTo(x, y)
                    } else {
                        if (screenHeight < stHeight + y && screenWidth > stWidth + x) {
                            y = y - eleH - 10;
                            sf.st.addClass("animated zoomInUpPopup");
                            setTimeout(function() {
                                sf.st.removeClass("animated zoomInUpPopup")
                            }, 600);
                            sf.st.moveTo(x, y)
                        } else {
                            if (screenHeight > stHeight + y && screenWidth < stWidth + x) {
                                x = screenWidth - stWidth;
                                sf.st.addClass("animated zoomInDownPopup");
                                setTimeout(function() {
                                    sf.st.removeClass("animated zoomInDownPopup")
                                }, 600);
                                sf.st.moveTo(x, y)
                            } else {
                                x = screenWidth - stWidth;
                                y = y - eleH - 10;
                                sf.st.addClass("animated zoomInUpPopup");
                                setTimeout(function() {
                                    sf.st.removeClass("animated zoomInUpPopup")
                                }, 600);
                                sf.st.moveTo(x, y)
                            }
                        }
                    }
                }
            } else {
                sf.creatListView(ele);
                var stHeight = sf.st.getHeight();
                var stWidth = sf.st.getWidth();
                if (screenHeight > stHeight + y && screenWidth > stWidth + x) {
                    sf.st.addClass("animated zoomInDownPopup");
                    setTimeout(function() {
                        sf.st.removeClass("animated zoomInDownPopup")
                    }, 600);
                    sf.st.moveTo(x, y)
                } else {
                    if (screenHeight < stHeight + y && screenWidth > stWidth + x) {
                        y = y - eleH - 10;
                        sf.st.addClass("animated zoomInUpPopup");
                        setTimeout(function() {
                            sf.st.removeClass("animated zoomInUpPopup")
                        }, 600);
                        sf.st.moveTo(x, y)
                    } else {
                        if (screenHeight > stHeight + y && screenWidth < stWidth + x) {
                            x = screenWidth - stWidth;
                            sf.st.addClass("animated zoomInDownPopup");
                            setTimeout(function() {
                                sf.st.removeClass("animated zoomInDownPopup")
                            }, 600);
                            sf.st.moveTo(x, y)
                        } else {
                            x = screenWidth - stWidth;
                            y = y - eleH - 10;
                            sf.st.addClass("animated zoomInUpPopup");
                            setTimeout(function() {
                                sf.st.removeClass("animated zoomInUpPopup")
                            }, 600);
                            sf.st.moveTo(x, y)
                        }
                    }
                }
            }
        },
        syncScroll: function(e, t) {
            var sf = this;
            sf.hideEditor();
            sf.uh.dom.scrollLeft = sf.ub.dom.scrollLeft;
            if (sf.lb) {
                if (sf.lb.dom === t) {
                    sf.ub.dom.scrollTop = sf.lb.dom.scrollTop
                } else {
                    sf.lb.dom.scrollTop = sf.ub.dom.scrollTop
                }
            }
            if (sf.uf) {
                sf.uf.dom.scrollLeft = sf.ub.dom.scrollLeft
            }
        },
        handleKeyUp: function(e) {
            if (e.getKey() == 9) {
                this.showEditorByRecord()
            }
        },
        handleKeyDown: function(e) {
            var sf = this,
                key = e.getKey(),
                ds = sf.dataset;
            if (e.ctrlKey && e.keyCode == 86 && sf.canpaste) {
                var text = window.clipboardData.getData("text");
                if (text) {
                    EACH(text.split("\n"), function(row) {
                        var values = row.split("\t");
                        if (values == _N) {
                            return
                        }
                        var data = {},
                            v = 0;
                        EACH(sf.columns, function(c) {
                            if (sf.isFunctionCol(c.type)) {
                                return
                            }
                            if (c.hidden !== TRUE) {
                                data[c.name] = values[v];
                                v++
                            }
                        });
                        ds.create(data)
                    })
                }
            } else {
                if (key == 9) {
                    sf.showEditorByRecord()
                } else {
                    if (key == 38 || key == 40 || key == 33 || key == 34) {
                        if (ds.loading == TRUE) {
                            return
                        }
                        switch (e.getKey()) {
                            case 33:
                                ds.prePage();
                                break;
                            case 34:
                                ds.nextPage();
                                break;
                            case 38:
                                if (!e.ctrlKey) {
                                    ds.pre()
                                }
                                break;
                            case 40:
                                if (!e.ctrlKey) {
                                    ds.next()
                                }
                                break
                        }
                        e.stopEvent()
                    }
                }
            }
            sf.fireEvent(EVT_KEY_DOWN, sf, e)
        },
        processDataSetLiestener: function(ou) {
            var sf = this,
                ds = sf.dataset;
            if (ds) {
                ds[ou]("ajaxfailed", sf.onAjaxFailed, sf);
                ds[ou]("metachange", sf.onRefresh, sf);
                ds[ou]("update", sf.onUpdate, sf);
                ds[ou]("reject", sf.onUpdate, sf);
                ds[ou]("add", sf.onAdd, sf);
                ds[ou]("submit", sf.onBeforSubmit, sf);
                ds[ou]("submitfailed", sf.onAfterSuccess, sf);
                ds[ou]("submitsuccess", sf.onAfterSuccess, sf);
                ds[ou]("query", sf.onBeforeLoad, sf);
                ds[ou]("load", sf.onLoad, sf);
                ds[ou]("loadfailed", sf.onAjaxFailed, sf);
                ds[ou]("valid", sf.onValid, sf);
                ds[ou]("beforeremove", sf.onBeforeRemove, sf);
                ds[ou]("remove", sf.onLoad, sf);
                ds[ou]("clear", sf.onLoad, sf);
                ds[ou]("refresh", sf.onRefresh, sf);
                ds[ou]("fieldchange", sf.onFieldChange, sf);
                ds[ou]("indexchange", sf.onIndexChange, sf);
                ds[ou]("select", sf.onSelect, sf);
                ds[ou]("unselect", sf.onUnSelect, sf);
                ds[ou]("selectall", sf.onSelectAll, sf);
                ds[ou]("unselectall", sf.onUnSelectAll, sf);
                ds[ou]("wait", sf.onWait, sf);
                ds[ou]("afterwait", sf.onAfterSuccess, sf)
            }
        },

        resetPageSize:function(){
            var sf = this;
            ds = sf.dataset;
            ds.pagesize = Math.max(0, Math.floor(((parseInt(("" + sf.ub.getStyle("max-height")).replace("px", "")) || parseInt(("" + sf.ub.getStyle("height")).replace("px", "")) || sf.ub.getHeight()) || parseFloat(sf.ub.dom.style.height)) / ROW_HEIGHT));
            if (isNaN(ds.pagesize) || ds.pagesize == 0) {
                ds.pagesize = 1
            }
        },
        bind: function(ds) {
            if (Ext.isString(ds)) {
                ds = $le(ds);
                if (!ds) {
                    return
                }
            }
            var sf = this;
            sf.dataset = ds;
            sf.processDataSetLiestener(ON);
            if (ds.autopagesize === TRUE && ds.fetchall !== TRUE) {
                ds.pagesize = Math.max(0, Math.floor(((parseInt(("" + sf.ub.getStyle("max-height")).replace("px", "")) || parseInt(("" + sf.ub.getStyle("height")).replace("px", "")) || sf.ub.getHeight()) || parseFloat(sf.ub.dom.style.height)) / ROW_HEIGHT));
                if (isNaN(ds.pagesize) || ds.pagesize == 0) {
                    ds.pagesize = 1
                }
                if (ds.getAll().length || ds.qtId) {
                    ds.query();
                    return
                }
            }
            sf.onLoad()
        },
        initTemplate: function() {
            var sf = this;
            sf.rowTdTpl = new Ext.Template(["<td {rowSpan} ", ATYPE, '="{', ATYPE, '}" class="', GRID_ROWBOX, ' item-ckb-self" ', RECORD_ID, '="{', RECORD_ID, '}">']);
            sf.rowNumTdTpl = new Ext.Template(['<td {rowSpan} style="text-align:{align}" class="', GRN_ROW_NUMBER, '" ', ATYPE, '="', GRN_ROW_NUMBER, '" ', RECORD_ID, '="{', RECORD_ID, '}">']);
            sf.rowNumCellTpl = new Ext.Template(['<div class="', GRID_CELL, '">{text}</div>']);
            sf.tdTpl = new Ext.Template(['<td class="{celltdcls}" {rowSpan} style="visibility:{visibility};text-align:{align}" ', DATA_INDEX, '="{name}" ', ATYPE, '="', GRID_CELL, '" ', RECORD_ID, '="{', RECORD_ID, '}">']);
            sf.cellTpl = new Ext.Template(['<div class="', GRID_CELL, ' {cellcls}" style="', WIDTH, ":{", WIDTH, '}px" id="', sf.id, '_{name}_{recordid}" title="{title}">{celleditordiv}<span>{text}</span></div>']);
            sf.cbTpl = new Ext.Template(['<center><div class="{cellcls}" id="', sf.id, "_{name}_{", RECORD_ID, '}"></div></center>'])
        },
        getCheckBoxStatus: function(record, name, readonly) {
            var field = record.getField(name),
                cv = field.get(CHECKED_VALUE),
                value = record.data[name];
            return ITEM_CKB + (readonly ? READONLY : _N) + ((value && value == cv) ? C : U)
        },
        createTemplateData: function(col, record) {
            return {
                recordid: record.id,
                visibility: col.hidden === TRUE ? HIDDEN : VISIBLE,
                name: col.name
            }
        },
        createCell: function(col, record, td, rowSpan) {
            var sf = this,
                data = sf.createTemplateData(col, record),
                cellTpl, tdTpl = sf.tdTpl,
                cls = _N,
                cls_td = _N,
                xtype = col.type,
                readonly, editor = sf.getEditor(col, record, TRUE),
                sb = [];
            if (editor != _N) {
                var edi = A.CmpManager.get(editor);
                if (edi && (edi instanceof CheckBox)) {
                    xtype = CELL_CHECK
                } else {
                    if (sf.editorborder) {
                        cls = CELL_EDITOR
                    } else {
                        data["celleditordiv"] = '<div class="cell-editor-div"></div>'
                    }
                }
            } else {
                if (col.name && !IS_EMPTY(record.getField(col.name).get(CHECKED_VALUE))) {
                    xtype = CELL_CHECK;
                    readonly = TRUE
                }
            }
            if (xtype == ROW_CHECK || xtype == ROW_RADIO) {
                var ds = sf.dataset,
                    ck = ds.getSelected().indexOf(record) == -1 ? U : C;
                readonly = ds.execSelectFunction(record) ? _N : READONLY;
                tdTpl = sf.rowTdTpl;
                Ext.apply(data, {
                    align: CENTER,
                    atype: xtype == ROW_CHECK ? GRID$ROWCHECK : GRID$ROWRADIO,
                    cellcls: xtype == ROW_CHECK ? GRID_CKB + ITEM_CKB + readonly + ck : "grid-radio " + ITEM_RADIO_IMG + readonly + ck
                });
                cellTpl = sf.cbTpl
            } else {
                if (xtype == CELL_CHECK) {
                    Ext.apply(data, {
                        align: CENTER,
                        cellcls: GRID_CKB + sf.getCheckBoxStatus(record, col.name, readonly)
                    });
                    cellTpl = sf.cbTpl
                } else {
                    var field = record.getMeta().getField(col.name);
                    if (field && IS_EMPTY(record.data[col.name]) && field.get(REQUIRED) == TRUE) {
                        cls = cls + _S + ITEM_NOT_BLANK
                    }
                    if (field && field.get(REQUIRED) == TRUE) {
                        cls = cls + _S + ITEM_NOT_SPE_BLANK
                    }
                    var t = sf.renderText(record, col, record.data[col.name]);
                    Ext.apply(data, {
                        align: col.align || LEFT,
                        cellcls: cls,
                        celltdcls: cls_td,
                        text: t,
                        title: col.showtitle ? $L.unescapeHtml(String(t).replace(/<[^<>]+>/mg, _N)) : ""
                    });
                    if (xtype == ROW_NUMBER) {
                        tdTpl = sf.rowNumTdTpl;
                        cellTpl = sf.rowNumCellTpl
                    } else {
                        cellTpl = sf.cellTpl
                    }
                }
            }
            if (rowSpan) {
                data["rowSpan"] = "rowSpan=" + rowSpan
            }
            if (!td) {
                sb.push(tdTpl.applyTemplate(data))
            } else {
                if (td != TRUE && cls_td) {
                    Ext.fly(td).addClass(cls_td)
                }
            }
            sb.push(cellTpl.applyTemplate(data));
            if (!td) {
                sb.push("</td>")
            }
            return sb.join(_N)
        },
        createRow: function(type, row, cols, item, rowspans, first) {
            var sf = this;
            if (Ext.isArray(item)) {
                return item.map(function(d, i, colname) {
                    if (colname = d.colname) {
                        rowspans = rowspans || {};
                        rowspans[colname] = concatGroups(d).length;
                        rowspans[colname + "_count"] = TRUE
                    }
                    return sf.createRow(type, row, cols, d, rowspans, !i)
                }).join("")
            } else {
                var obj = {},
                    rowSpan = NULL,
                    found = FALSE,
                    css = sf.parseCss(sf.renderRow(item, row));
                sf.fireEvent(EVT_CREATE_ROW, sf, row, item, obj, cols);
                if (obj.height) {
                    css.style = (css.style || "") + ";height:" + obj.height + "px;"
                }
                var sb = ['<tr id="', sf.id, type, item.id, '"  _row="' + item.id + '" class="', (row % 2 == 0 ? _N : ROW_ALT), _S, css.cls, '"', 'style="', css.style, '" height="25">'];
                EACH(cols, function(col) {
                    if (col.hidden && col.visiable == FALSE) {
                        return
                    }
                    var colname;
                    if (sf.isFunctionCol(col.type)) {
                        colname = "__rowbox__"
                    } else {
                        colname = col.name;
                        if (obj.name && !obj.height && !found) {
                            rowSpan = NULL;
                            if (colname == obj.name) {
                                found = TRUE
                            } else {
                                rowSpan = 2
                            }
                        }
                    }
                    rowSpan = rowspans && rowspans[colname];
                    if (!rowSpan || rowspans[colname + "_count"]) {
                        sb.push(sf.createCell(col, item, NULL, rowSpan))
                    }
                    if (rowspans && rowspans[colname + "_count"]) {
                        rowspans[colname + "_count"] = FALSE
                    }
                });
                sb.push("</tr>");
                sb.push(obj.html || "");
                return sb.join(_N)
            }
        },
        parseCss: function(css) {
            var style = _N,
                cls = _N;
            if (Ext.isArray(css)) {
                for (var i = 0; i < css.length; i++) {
                    var _css = this.parseCss(css[i]);
                    style += ";" + _css.style;
                    cls += _S + _css.cls
                }
            } else {
                if (Ext.isString(css)) {
                    var isStyle = !! css.match(/^([^,:;]+:[^:;]+;)*[^,:;]+:[^:;]+;*$/);
                    cls = isStyle ? _N : css;
                    style = isStyle ? css : _N
                }
            }
            return {
                style: style,
                cls: cls
            }
        },
        renderText: function(record, col, value) {
            var renderer = col.renderer,
                value = $L.escapeHtml(value);
            if (renderer) {
                var rder = A.getRenderer(renderer);
                if (rder == NULL) {
                    alert(NOT_FOUND + renderer + METHOD);
                    return value
                }
                value = rder(value, record, col.name)
            }
            return value == NULL ? _N : value
        },
        renderRow: function(record, rowIndex) {
            var renderer = this.rowrenderer,
                css = NULL;
            if (renderer) {
                var rder = A.getRenderer(renderer);
                if (rder == NULL) {
                    alert(NOT_FOUND + renderer + METHOD);
                    return css
                }
                css = rder(record, rowIndex);
                return !css ? _N : css
            }
            return css
        },
        createTH: function(cols) {
            var sb = ['<tr class="grid-hl">'];
            for (var i = 0, l = cols.length; i < l; i++) {
                var c = cols[i];
                if (c.hidden && c.visiable == FALSE) {
                    continue
                }
                sb.push("<th ", DATA_INDEX, '="', c.name, '" style="height:0px;width:', c.width, PX, '"></th>')
            }
            sb.push("</tr>");
            return sb.join(_N)
        },
        onBeforeRemove: function() {
            A.Masker.mask(this.wb, _lang["grid.mask.remove"])
        },
        onWait: function() {
            if (this.submask == TRUE) {
                A.Masker.mask(this.wb, _lang["grid.mask.waiting"])
            }
        },
        onBeforeLoad: function() {
            this.ub.scrollTo(LEFT, 0);
            this.uh.scrollTo(LEFT, 0);
            if (this.submask == TRUE) {
                A.Masker.mask(this.wb, _lang["grid.mask.loading"])
            }
        },
        onBeforSubmit: function(ds) {
            if (this.submask == TRUE) {
                A.Masker.mask(this.wb, _lang["grid.mask.submit"])
            }
        },
        onFetching: function(ed) {
            var sf = this;
            sf.isFetching = TRUE;
            A.Masker.mask(sf.wb, _lang["grid.mask.fetching"]);
            ed.on("fetched", sf.onFetched, sf)
        },
        onFetched: function(ed) {
            var sf = this;
            sf.isFetching = FALSE;
            A.Masker.unmask(sf.wb);
            ed.un("fetched", sf.onFetched, sf);
            ed.un("fetching", sf.onFetching, sf)
        },
        onAfterSuccess: function() {
            A.Masker.unmask(this.wb)
        },
        preLoad: function() {},
        onLoad: function() {
            var sf = this,
                ds = sf.dataset,
                cr = ds.getCurrentRecord(),
                len = ds.getAll().length;
            sf.isSelectAll = FALSE;
            sf.clearDomRef();
            sf.preLoad();
            sf.processGroups();
            if (!len || ds.getSelected().length != len) {
                sf.wrap.removeClass(GRID_SELECT_ALL);
                sf.initHeadCheckStatus(FALSE)
            } else {
                sf.initHeadCheckStatus(TRUE)
            }
            if (sf.lb) {
                sf.renderLockArea()
            }
            sf.renderUnLockAread();
            sf.renderBottomScroll();
            sf.drawFootBar();
            cr && sf.onIndexChange(ds, cr);
            A.Masker.unmask(sf.wb);
            sf.fireEvent(EVT_RENDER, sf);
            //
            //自适应高度
            if(sf.dataset != null) {
                var length = sf.dataset.getAll().length;
                if(length == 0){
                    length = 5;
                }
                if(length < 6){
                    length = 5;
                }
                if (!sf.heightSeted) {
                    sf.setHeight(length * 29 + 36 + 39 + 29 + 29);
                }
                if (length >= 6) {
                    sf.heightSeted = true;
                }
            }
            $L.callSelectTab(sf);
            if($jq("#"+sf.id).closest(".item-tab").width()) {
                var tabWidth = $jq("#" + sf.id).closest(".item-tab").width();
                if ((sf.width + 30) > tabWidth) {
                    sf.setWidth($jq("#" + sf.id).closest(".item-tab").width() - 30);
                }
            }
            /*grid渲染完成后渲染nicescroll gaoyang*/
            /*$jq("#"+sf.ub.id).niceScroll({
                nativeparentscrolling: true
            });*/
        },
        processGroups: function() {
            var sf = this,
                ds = sf.dataset,
                colnames = [];
            sf.groups = [].concat(ds.getAll());
            Ext.each(sf.columns, function(col) {
                col.group && colnames.push(col.name)
            });
            if (colnames.length) {
                concatGroups(sf.groups = sortByGroup(sf.groups, colnames))
            }
        },
        initHeadCheckStatus: function(check) {
            var sf = this,
                cb = sf.wrap.child(SELECT_DIV_ATYPE);
            if (cb && sf.selectable && sf.selectionmodel == MULTIPLE) {
                sf.setCheckBoxStatus(cb, check)
            }
        },
        clearDomRef: function() {
            this.selectlockTr = NULL;
        },
        customize: function(ctx) {
            var path = location.pathname,
                str = path.indexOf("modules");
            if (str == -1) {
                str = path.indexOf(ctx) + ctx.length + 1
            }
            var screen_path = path.substring(str, path.length),
                screen = screen_path.substring(screen_path.lastIndexOf("/") + 1, screen_path.length),
                context_path = path.substring(0, str),
                parent = this.wrap.parent("[url]");
            if (parent) {
                var url = parent.getAttributeNS("", "url");
                if (url) {
                    url = url.split("?")[0];
                    if (url.indexOf(context_path) == -1) {
                        var li = url.lastIndexOf("/");
                        if (li != -1) {
                            url = url.substring(li + 1, url.length)
                        }
                        screen_path = screen_path.replaceAll(screen, url)
                    } else {
                        screen_path = url.substring(url.indexOf(context_path) + new String(context_path).length, url.length)
                    }
                }
            }
            new Leaf.Window({
                id: "sys_customization_grid",
                url: context_path + "modules/sys/sys_customization_grid.lview?source_file=" + screen_path + "&id=" + this.id + "&did=" + this.dataset.id,
                title: "个性化设置",
                height: 530,
                width: 560
            })
        },
        onAjaxFailed: function(res, opt) {
            A.Masker.unmask(this.wb)
        },
        onMouseWheel: function(e) {
            // if($jq(e.target).hasClass("grid-cell")){
            //     e.stopPropagation();
            //     e.stopEvent();
            // }
            if (this.editing == TRUE) {
                this.hideEditor();
                return;
            }
            var delta = e.getWheelDelta(),
                ds = this.dataset;
           // if(ds.isModified()){
           //     e.stopEvent();
           // }
            if (delta > 0) {
                ds.pre()
            } else {
                if (delta < 0) {
                    ds.next()
                }
            }
        },
        onMouseDown: function(e, t) {
            t = (t = Ext.fly(t)).is(TD) ? t : t.parent(TD);
            var sf = this,
                atype = t.getAttribute(ATYPE),
                ced;
            if ((ced = sf.currentEditor) && ced.editor instanceof CheckBox && atype == GRID_CELL && t.getAttribute(DATA_INDEX) == ced.name) {
                if (e.shiftKey) {
                    sf._begin = ced.record;
                    e.stopEvent()
                } else {
                    if ((t = t.child(".grid-ckb")) && t.dom.className.search(/readonly/) == -1) {
                        e.stopEvent();
                        ced.editor.focus.defer(Ext.isIE ? 1 : 0, ced.editor)
                    }
                }
            }
        },
        focus: function() {
            this.wb.focus()
        },
        onFocus: function() {
            var sf = this;
            this.hasFocus = TRUE;
            if (!sf.wrap.hasClass("gridShine")) {
                sf.wrap.addClass("gridShine")
            }
        },
        onUbFocus: function() {
            var sf = this;
            this.hasFocus = TRUE;
            if (!sf.wrap.hasClass("gridShine")) {
                sf.wrap.addClass("gridShine")
            }
        },
        onUbBlur: function() {
            var sf = this;
            sf.hasFocus = FALSE;
            if (sf.wrap.hasClass("gridShine")) {
                sf.wrap.removeClass("gridShine")
            }
        },
        blur: function() {
            this.wb.blur()
        },
        onBlur: function() {
            var sf = this;
            sf.hasFocus = FALSE;
            if (sf.wrap.hasClass("gridShine")) {
                sf.wrap.removeClass("gridShine")
            }
        },
        renderLockArea: function() {
            var sf = this,
                cols = sf.lockColumns,
                sb = ['<TABLE cellSpacing="0" atype="grid.lbt" cellPadding="0" border="0"  ', WIDTH, '="', sf.lockWidth, '"><TBODY>', sf.createTH(cols)];
            EACH(sf.groups, function(d, i) {
                var rowspans, colname;
                if (colname = d.colname) {
                    rowspans = {};
                    rowspans[colname] = concatGroups(d).length;
                    rowspans[colname + "_count"] = TRUE;
                    if (sf.groupselect) {
                        rowspans["__rowbox__"] = rowspans[colname];
                        rowspans["__rowbox___count"] = rowspans[colname + "_count"]
                    }
                }
                sb.push(sf.createRow($L_copy, i, cols, d, rowspans, !i))
            }, sf);
            sb.push('</TBODY></TABLE><DIV class="grid-bottom-scroll" style="height:11px"></DIV>');
            sf.lbt = sf.lb.update(sb.join(_N)).child("table[atype=grid.lbt]")
        },
        renderUnLockAread: function() {
            var sf = this,
                cols = sf.unlockColumns,
                sb = ['<TABLE cellSpacing="0" atype="grid.ubt" cellPadding="0" border="0" ', WIDTH, '="', sf.unlockWidth, '"><TBODY>', sf.createTH(cols)];
            EACH(sf.groups, function(d, i) {
                var rowspans;
                if (d.colname) {
                    rowspans = {};
                    rowspans[d.colname] = concatGroups(d).length;
                    rowspans[d.colname + "_count"] = TRUE
                }
                sb.push(sf.createRow($U, i, cols, d, rowspans, !i))
            }, sf);
            sb.push("</TBODY></TABLE>");
            sf.ubt = sf.ub.update(sb.join(_N)).child("table[atype=grid.ubt]")
        },
        renderBottomScroll: function() {
            var sf = this;
            if (sf.lb) {
                try {
                    if (sf.ub.dom.scrollWidth > sf.ub.dom.clientWidth) {
                        sf.lb.child(".grid-bottom-scroll").setHeight("11px")
                    } else {
                        sf.lb.child(".grid-bottom-scroll").setHeight("0px")
                    }
                } catch (e) {}
            }
        },
        isOverSplitLine: function(x) {
            var sf = this,
                v = 0,
                isOver = FALSE;
            sf.overColIndex = -1;
            EACH(sf.columns, function(c, i) {
                if (c.hidden !== TRUE) {
                    v += c.width
                }
                if (x < v + 3 && x > v - 3 && c.resizable != FALSE) {
                    isOver = TRUE;
                    sf.overColIndex = i;
                    return FALSE
                }
            });
            return isOver
        },
        onRefresh: function() {
            var sf = this;
            sf.onLoad();
            if (sf.selectable) {
                var ds = sf.dataset;
                EACH(ds.selected, function(s) {
                    sf.onSelect(ds, s)
                })
            }
        },
        onIndexChange: function(ds, r) {
            var index = this.getDataIndex(r.id);
            if (index == -1) {
                return
            }
            this.selectRow(index, FALSE)
        },
        isFunctionCol: function(t) {
            return t == ROW_CHECK || t == ROW_RADIO || t == ROW_NUMBER
        },
        onAdd: function(ds, record, row, fire) {
            var __row = row,
                sf = this,
                columns = sf.columns,
                groups = sf.groups,
                glength = groups.length,
                dlength = ds.data.length,
                css = sf.parseCss(sf.renderRow(record, row)),
                cls = ((glength == dlength ? row : glength) % 2 == 0 ? _N : ROW_ALT);
            addIntoGroup(groups, record, columns);
            if (sf.lbt) {
                var ltr = Ext.get(DOC.createElement(TR)),
                    ltb = sf.lbt.child("tbody:first");
                ltr.set({
                    id: sf.id + $L_copy + record.id,
                    style: css.style,
                    _row: record.id,
                    height: 25
                })
            }
            var utr = Ext.get(DOC.createElement(TR)),
                utb = sf.ubt.child("tbody:first");
            utr.set({
                id: sf.id + $U + record.id,
                style: css.style,
                _row: record.id,
                height: 25
            });
            EACH(columns, function(col) {
                if (col.hidden && col.visiable == FALSE) {
                    return TRUE
                }
                var colname = col.name,
                    find = FALSE;
                if (col.group) {
                    var allRow = 0;
                    sf[col.lock === TRUE ? "lbt" : "ubt"].select(SELECT_TD_DATAINDEX + colname + _K).each(function(td, tds) {
                        var _r = sf.dataset.findById(td.parent(TR).getAttributeNS(_N, "_row")),
                            value = record.get(colname);
                        if (_r && !IS_EMPTY(value) && _r.get(colname) == value) {
                            var _groups = searchGroup(groups, value),
                                rowspan = concatGroups(_groups).length;
                            allRow += rowspan;
                            td.dom.rowSpan = rowspan;
                            find = TRUE;
                            row = allRow - 1;
                            cls = td.parent(TR).hasClass(ROW_ALT) ? ROW_ALT : _N;
                            if (groups.indexOf(_groups) != -1 && columns[0].type) {
                                sf.lbt.query("td[recordid=" + _r.id + "]:first")[0].rowSpan = rowspan;
                                if (record !== _r) {
                                    ltr.select("td[recordid=" + record.id + "]:first").remove()
                                }
                            }
                            return FALSE
                        } else {
                            allRow += td.dom.rowSpan
                        }
                    });
                    if (!find) {
                        row = __row
                    }
                }
                if (find) {
                    return
                }
                var td = Ext.get(DOC.createElement(TD));
                if (col.type == ROW_CHECK) {
                    td.set({
                        recordid: record.id,
                        atype: GRID$ROWCHECK
                    }).addClass(GRID_ROWBOX);
                    if (sf.isSelectAll) {
                        td.addClass(ITEM_CKB_SELF)
                    }
                } else {
                    if (col.type == ROW_RADIO) {
                        td.set({
                            recordid: record.id,
                            atype: GRID$ROWRADIO
                        }).addClass(GRID_ROWBOX)
                    } else {
                        if (col.hidden) {
                            td.hide()
                        }
                        td.setStyle({
                            "text-align": col.align || LEFT
                        });
                        var data = {
                            recordid: record.id,
                            atype: GRID_CELL
                        };
                        if (!sf.isFunctionCol(col.type)) {
                            data.dataindex = colname
                        }
                        if (col.type == ROW_NUMBER) {
                            td.addClass(GRN_ROW_NUMBER);
                            data.atype = GRN_ROW_NUMBER
                        }
                        td.set(data)
                    }
                }
                td.update(sf.createCell(col, record, td));
                if (col.lock === TRUE) {
                    ltr.appendChild(td)
                } else {
                    utr.appendChild(td)
                }
            });
            ltr && ltr.addClass(cls + _S + css.cls);
            utr.addClass(cls + _S + css.cls);
            if (row === dlength - 1) {
                ltb && ltb.appendChild(ltr);
                utb.appendChild(utr)
            } else {
                var filter = function(el, i) {
                        if (i < row) {
                            return FALSE
                        }
                    },
                    ltrs = ltb && ltb.select(SELECT_TR_CLASS).filter(filter),
                    trs = utb.select(SELECT_TR_CLASS).filter(filter),
                    tr = trs.item(0),
                    needToggleClass = (tr && cls == (tr.hasClass(ROW_ALT) ? ROW_ALT : _N)),
                    root = searchRootGroupByRecord(record);
                ltr && ltr.insertBefore(ltrs.item(0));
                utr.insertBefore(tr);
                needToggleClass && setTimeout(function() {
                    filter = function(tr) {
                        if (searchRootGroupByRecord(ds.findById(tr.getAttributeNS(_N, "_row"))) === root) {
                            return FALSE
                        }
                    };
                    ltrs && ltrs.filter(filter).toggleClass(ROW_ALT);
                    trs.filter(filter).toggleClass(ROW_ALT)
                }, 1);
                ltrs && ltrs.each(function(tr) {
                    tr.select(".grid-rownumber div").each(function(div) {
                        div.update(Number(div.dom.innerHTML) + 1)
                    });
                    tr.select($GRID_ROWBOX).each(function(td) {
                        sf.setSelectStatus(ds.findById(td.getAttributeNS(_N, RECORD_ID)))
                    })
                })
            }
            sf.setSelectStatus(record);
            sf.drawFootBar();
            if (fire !== FALSE) {
                sf.fireEvent(EVT_ADD_ROW, sf, record)
            }
            sf.renderBottomScroll();
            /*$jq("#"+sf.ub.id).getNiceScroll().remove();
            $jq("#"+sf.ub.id).niceScroll({nativeparentscrolling: true});*/

            //自适应高度
            if(sf.dataset != null) {
                var length = sf.dataset.getAll().length;
                if(length == 0){
                    length = 5;
                }
                if(length < 6){
                    length = 5;
                }
                sf.setHeight(length * 29 + 36 + 39 + 29 + 29 + 29);
            }
        },
        renderEditor: function(div, record, c, editor) {
            var td = div.parent(TD);
            td.update(this.createCell(c, record, td.dom))
        },
        onUpdate: function(ds, record, name, value, oldvalue) {
            var sf = this,
                c = sf.findColByName(name),
                div, ediv, text;
            if (c && c.group) {
                record.data[name] = oldvalue;
                var index = sf.ubt.query(SELECT_TR_CLASS).indexOf(sf.ubt.child("tr[_row=" + record.id + "]").dom);
                sf.onRemove(ds, record);
                record.data[name] = value;
                sf.onAdd(ds, record, index, FALSE);
                !IS_EMPTY(oldvalue) && EACH(concatGroups(searchGroup(sf.groups, oldvalue)), function(r) {
                    if (r !== record) {
                        r.set(name, value)
                    }
                });
                sf.selectRow(sf.dataset.indexOf(record))
            }
            if (div = Ext.get([sf.id, name, record.id].join(_))) {
                var editor = sf.getEditor(c, record);
                if (editor != _N ? ($le(editor) instanceof CheckBox) : (name && !IS_EMPTY(record.getField(name).get(CHECKED_VALUE)))) {
                    sf.renderEditor(div, record, c, editor)
                } else {
                    (div.child("span") || div).update(text = sf.renderText(record, c, value));
                    c.showtitle && div.set({
                        "title": $L.unescapeHtml(String(text).replace(/<[^<>]+>/mg, _N))
                    })
                }
            }
            EACH(sf.columns, function(c) {
                if (c.name != name && (ediv = Ext.get([sf.id, c.name, record.id].join(_)))) {
                    if (c.editorfunction || c.editor) {
                        sf.renderEditor(ediv, record, c, sf.getEditor(c, record))
                    }
                    if (c.renderer) {
                        (ediv.child("span") || ediv).update(text = sf.renderText(record, c, record.get(c.name)));
                        c.showtitle && ediv.set({
                            "title": $L.unescapeHtml(String(text).replace(/<[^<>]+>/mg, _N))
                        })
                    }
                }
            });
            sf.setSelectStatus(record);
            sf.drawFootBar(name)
        },
        onValid: function(ds, record, name, valid) {
            var c = this.findColByName(name);
            if (c) {
                var div = findBorderParent.call(this, Ext.get([this.id, name, record.id].join(_)));
                if (div) {
                    if (valid == FALSE) {
                        div.addClass(ITEM_INVALID)
                    } else {
                        div.removeClass([ITEM_INVALID])
                    }
                }
            }
        },
        onRemove: function(ds, record) {
            var sf = this,
                lrow = Ext.get(sf.id + $L_copy + record.id),
                urow = Ext.get(sf.id + $U + record.id);
            if (lrow) {
                lrow.remove()
            }
            if (urow) {
                urow.remove()
            }
            removeFromGroup(sf.groups, record);
            EACH(sf.columns, function(col) {
                var colname = col.name;
                if (colname) {
                    sf.removeCompositeEditor(colname, record)
                }
                if (col.group) {
                    sf.wrap.select(SELECT_TD_DATAINDEX + colname + _K).each(function(td) {
                        var _r = sf.dataset.findById(td.parent(TR).getAttributeNS(_N, "_row")),
                            rowspan;
                        if (_r && _r.get(colname) == record.get(colname)) {
                            if ((rowspan = td.getAttribute("rowspan")) && (rowspan -= 1) > 1) {
                                td.dom.rowSpan = rowspan
                            }
                            return FALSE
                        }
                    })
                }
            });
            if (Ext.isIE || Ext.isIE9) {
                sf.syncScroll()
            }
            sf.clearDomRef();
            A.Masker.unmask(sf.wb);
            sf.drawFootBar()
        },
        onClear: function() {},
        onFieldChange: function(ds, record, field, type, value) {
            if (type == REQUIRED) {
                var div = findBorderParent.call(this, Ext.get([this.id, field.name, record.id].join(_)));
                if (div) {}
            }
        },
        getDataIndex: function(rid) {
            for (var i = 0, data = this.dataset.data, l = data.length; i < l; i++) {
                if (data[i].id == rid) {
                    return i
                }
            }
            return -1
        },
        onSelect: function(ds, record, isSelectAll) {
            if (this.id === "_superlov_autogrid") {
                addRecord(ds, record, isSelectAll)
            }
            if (!record || isSelectAll) {
                return
            }
            var sf = this,
                cb = Ext.get(sf.id + __ + record.id);
            if (cb) {
                cb.parent($GRID_ROWBOX).addClass(ITEM_CKB_SELF);
                if (sf.selectionmodel == MULTIPLE) {
                    sf.setCheckBoxStatus(cb, TRUE);
                    if (ds.selected.length == ds.data.length) {
                        sf.initHeadCheckStatus(TRUE)
                    }
                } else {
                    sf.setRadioStatus(cb, TRUE);
                    ds.locate((ds.currentPage - 1) * ds.pagesize + ds.indexOf(record) + 1)
                }
                sf.setSelectStatus(record)
            }
            sf.groupselect && EACH(!isSelectAll && concatGroups(searchRootGroupByRecord(sf.groups, record)), function(r) {
                if (r !== record) {
                    ds.select(r)
                }
            })
        },
        onUnSelect: function(ds, record, isSelectAll) {
            if (this.id === "_superlov_autogrid") {
                removeRecord(ds, record, isSelectAll)
            }
            if (!record || isSelectAll) {
                return
            }
            var sf = this,
                cb = Ext.get(sf.id + __ + record.id);
            if (cb) {
                cb.parent($GRID_ROWBOX).addClass(ITEM_CKB_SELF);
                if (sf.selectionmodel == MULTIPLE) {
                    sf.setCheckBoxStatus(cb, FALSE);
                    sf.initHeadCheckStatus(FALSE)
                } else {
                    sf.setRadioStatus(cb, FALSE)
                }
                sf.setSelectStatus(record)
            }
            sf.groupselect && EACH(!isSelectAll && concatGroups(searchRootGroupByRecord(sf.groups, record)), function(r) {
                if (r !== record) {
                    ds.unSelect(r)
                }
            })
        },
        onSelectAll: function() {
            var sf = this;
            sf.clearChecked();
            sf.isSelectAll = TRUE;
            sf.isUnSelectAll = FALSE;
            sf.wrap.addClass(GRID_SELECT_ALL);
            sf.initHeadCheckStatus(TRUE)
        },
        onUnSelectAll: function() {
            var sf = this;
            sf.clearChecked();
            sf.isSelectAll = FALSE;
            sf.isUnSelectAll = TRUE;
            sf.wrap.removeClass(GRID_SELECT_ALL);
            sf.initHeadCheckStatus(FALSE)
        },
        clearChecked: function() {
            var w = this.wrap;
            w.select($ITEM_CKB_SELF_S$ITEM_CKB_C).replaceClass(ITEM_CKB_C, ITEM_CKB_U);
            w.select($ITEM_CKB_SELF).removeClass(ITEM_CKB_SELF)
        },
        onDblclick: function(e, t) {
            if (t = Ext.fly(t).parent("td[atype=grid-cell]")) {
                var sf = this,
                    ds = sf.dataset,
                    record = ds.findById(t.getAttributeNS(_N, RECORD_ID));
                sf.fireEvent(EVT_DBLCLICK, sf, record, ds.indexOf(record), t.getAttributeNS(_N, DATA_INDEX))
            }
        },
        onClick: function(e, t) {
            var target = (t = Ext.fly(t)).is(TD) ? t : t.parent(TD);
            if (target) {
                var sf = this,
                    atype = target.getAttributeNS(_N, ATYPE),
                    rid = target.getAttributeNS(_N, RECORD_ID),
                    ds = sf.dataset;
                if (atype == GRID_CELL) {
                    var record = ds.findById(rid),
                        row = ds.indexOf(record),
                        name = target.getAttributeNS(_N, DATA_INDEX);
                    sf.fireEvent(EVT_CELL_CLICK, sf, row, name, record, !t.hasClass("grid-ckb"));
                    sf.fireEvent(EVT_ROW_CLICK, sf, row, record)
                } else {
                    if (atype == GRN_ROW_NUMBER) {
                        var record = ds.findById(rid),
                            row = ds.indexOf(record);
                        if (record.id != sf.selectedId) {
                            sf.selectRow(row)
                        }
                    } else {
                        if (atype == GRID$ROWCHECK) {
                            var cb = Ext.get(sf.id + __ + rid);
                            if (cb.hasClass(ITEM_CKB_READONLY_U) || cb.hasClass(ITEM_CKB_READONLY_C)) {
                                return
                            }
                            if (sf.isSelectAll && !cb.parent($ITEM_CKB_SELF)) {
                                cb.replaceClass(ITEM_CKB_U, ITEM_CKB_C)
                            } else {
                                if (sf.isUnselectAll && !cb.parent($ITEM_CKB_SELF)) {
                                    cb.replaceClass(ITEM_CKB_C, ITEM_CKB_U)
                                }
                            }
                            cb.hasClass(ITEM_CKB_C) ? ds.unSelect(rid) : ds.select(rid)
                        } else {
                            if (atype == GRID$ROWRADIO) {
                                var cb = Ext.get(sf.id + __ + rid);
                                if (cb.hasClass(ITEM_RADIO_IMG_READONLY_U) || cb.hasClass(ITEM_RADIO_IMG_READONLY_C)) {
                                    return
                                }
                                ds.select(rid)
                            }
                        }
                    }
                }
            }
        },
        onCellClick: function(grid, row, name, record, callback) {
            this.adjustColumn(name);
            this.showEditor(row, name, callback)
        },
        adjustColumn: function(name) {
            return;
            var sf = this,
                col = sf.findColByName(name);
            if (!col || !col.autoadjust || col.hidden) {
                return
            }
            var th = sf.wrap.select("tr.grid-hl " + SELECT_TH_DATAINDEX + name + _K),
                w = parseInt(th.elements[0].style.width),
                max = w,
                margin = 0,
                width = Math.min(sf.width - (sf.selectable ? 23 : 0) - 20, col.maxadjustwidth || Number.MAX_VALUE);
            sf.wrap.select(SELECT_TD_DATAINDEX + name + "] span").each(function(span) {
                if (Ext.isIE || !Ext.isIE9) {
                    span.parent().setStyle(TEXT_OVERFLOW, "clip")
                }
                max = Math.max(span.getWidth() + margin, max);
                if (Ext.isIE || !Ext.isIE9) {
                    span.parent().setStyle(TEXT_OVERFLOW, _N)
                }
                if (max > width) {
                    max = width;
                    return FALSE
                }
            });
            if (max > w) {
                sf.setColumnSize(name, max)
            }
        },
        setColumnPrompt: function(name, prompt) {
            this.wrap.select("td.grid-hc" + SELECT_DATAINDEX + name + "] div").update(prompt);
            this.findColByName(name).prompt = prompt
        },
        setEditor: function(name, editor) {
            var col = this.findColByName(name),
                div = Ext.get([this.id, name, this.selectedId].join(_));
            col.editor = editor;
            if (div) {
                if (editor == _N) {
                    div.removeClass(CELL_EDITOR)
                } else {
                    if (!$le(editor) instanceof CheckBox) {
                        div.addClass(CELL_EDITOR)
                    }
                }
            }
        },
        getEditor: function(col, record, onCreate) {
            var ed = col.editor || _N;
            if (col.editorfunction) {
                var ef = A.getRenderer(col.editorfunction);
                if (ef == NULL) {
                    alert(NOT_FOUND + col.editorfunction + METHOD);
                    return NULL
                }
                ed = ef(record, col.name) || _N
            }
            if (!onCreate && ed && !Ext.get([this.id, col.name, record.id].join(_))) {
                ed = _N
            }
            return ed
        },
        positionEditor: function() {
            var sf = this,
                ced = sf.currentEditor,
                ed = ced.editor,
                isCheckBox = ed instanceof CheckBox,
                dom = findBorderParent.call(sf, Ext.get([sf.id, ced.name, ced.record.id].join(_)), isCheckBox),
                xy = dom.getXY();
            if (isCheckBox) {
                ed.move(xy[0], xy[1] - 3)
            } else {
                ed.move(xy[0], xy[1])
            }
        },
        showEditor: function(row, name, callback) {
            if (row == -1) {
                return
            }
            var sf = this,
                col = sf.findColByName(name);
            if(name == undefined){      //未定义name的字段被点击也需要进行selectRow操作
                sf.selectRow(row)
            }
            if (!col) {
                return
            }
            var ds = sf.dataset,
                record = ds.getAt(row);
            if (!record) {
                return
            }
            if (record.id != sf.selectedId) {
                sf.selectRow(row)
            } else {
                sf.focusRecord(record)
            }
            sf.focusColumn(name);
            var editor = sf.getEditor(col, record);
            sf.setEditor(name, editor);
            if (editor != _N) {
                var ed = $le(editor);
                (function() {
                    var v = record.get(name),
                        dom = findBorderParent.call(sf, Ext.get([sf.id, name, record.id].join(_))),
                        ced;
                    ed.bind(ds, name);
                    ed.render(record);
                    ed.el.on(EVT_KEY_DOWN, sf.onEditorKeyDown, sf);
                    Ext.fly(DOC_EL).on(EVT_MOUSE_DOWN, sf.onEditorBlur, sf);
                    ced = sf.currentEditor = {
                        record: record,
                        ov: v,
                        name: name,
                        editor: ed
                    };
                    sf.positionEditor();
                    if (ed instanceof CheckBox) {
                        var _begin = sf._begin;
                        if (sf._begin) {
                            var _begin_index = ds.indexOf(_begin),
                                _end_index = ds.indexOf(record);
                            if (_begin_index > _end_index) {
                                var t = _end_index;
                                _end_index = _begin_index;
                                _begin_index = t
                            }
                            _end_index++;
                            for (; _begin_index < _end_index; _begin_index++) {
                                ds.getAt(_begin_index).set(name, _begin.get(name))
                            }
                            delete sf._begin
                        } else {
                            if (callback) {
                                ed.focus()
                            } else {
                                ed.onClick()
                            }
                        }
                    } else {
                        if (ed instanceof A.Field && !ed instanceof A.TextArea) {
                            ed.el.setStyle("text-align", col.align || LEFT)
                        } else {
                            if (ed instanceof A.Lov) {
                                ed.on("fetching", sf.onFetching, sf)
                            }
                        }
                        if (!(ed instanceof A.TextArea)) {
                            ed.setHeight(dom.getHeight() - 2)
                        }
                        ed.setWidth(dom.getWidth() - 3);
                        ed.isEditor = TRUE;
                        ed.isFireEvent = TRUE;
                        ed.isHidden = FALSE;
                        ed.focus();
                        ed.on(EVT_SELECT, sf.onEditorSelect, sf);
                        if (Ext.isFunction(callback)) {
                            callback(ed)
                        }
                        sf.fireEvent(EVT_EDITOR_SHOW, sf, ed, row, name, record)
                    }
                    sf.editing = TRUE
                }).defer(10)
            }
        },
        showEditorByRecord: function(record) {
            var sf = this,
                ds = sf.dataset;
            row = record ? ds.indexOf(record) : 0;
            record = record || ds.getAt(0);
            if (!record && sf.autoappend) {
                record = ds.create()
            }
            if (record) {
                EACH(sf.columns, function(col) {
                    if (col.hidden != TRUE && sf.getEditor(col, record) != _N) {
                        sf.fireEvent(EVT_CELL_CLICK, sf, row, col.name, record, function() {});
                        sf.fireEvent(EVT_ROW_CLICK, sf, row, record);
                        return FALSE
                    }
                })
            }
        },
        onEditorSelect: function() {
            (function() {
                this.hideEditor()
            }).defer(1, this)
        },
        onEditorKeyDown: function(e) {
            var sf = this,
                keyCode = e.keyCode,
                ced = sf.currentEditor;
            if (keyCode == 27) {
                if (ced) {
                    var ed = ced.editor;
                    if (ed) {
                        ed.clearInvalid();
                        ed.render(ed.binder.ds.getCurrentRecord())
                    }
                }
                sf.hideEditor()
            } else {
                if (keyCode == 13) {
                    if (!(ced && ced.editor && ced.editor instanceof A.TextArea)) {
                        sf.showEditorBy(39)
                    }
                } else {
                    if (keyCode == 9) {
                        e.stopEvent();
                        sf.showEditorBy(e.shiftKey ? 37 : 39)
                    } else {
                        if (e.ctrlKey && (keyCode == 37 || keyCode == 38 || keyCode == 39 || keyCode == 40)) {
                            sf.showEditorBy(keyCode);
                            e.stopEvent()
                        }
                    }
                }
            }
        },
        findEditorBy: function(dir) {
            var sf = this,
                ced, ed;
            if ((ced = sf.currentEditor) && (ed = ced.editor)) {
                var cls = sf.columns,
                    find = FALSE,
                    ds = sf.dataset,
                    fname = ed.binder.name,
                    r = ed.record,
                    row = ds.data.indexOf(r),
                    name = NULL,
                    hasAutoAppend = FALSE,
                    forward = TRUE,
                    updown = FALSE,
                    col;
                if (row != -1) {
                    if (dir == 37 || dir == 38) {
                        cls = [].concat(cls).reverse();
                        forward = FALSE
                    }
                    if (dir == 38 || dir == 40) {
                        updown = TRUE;
                        col = sf.findColByName(fname)
                    }
                    while (r) {
                        if (!updown) {
                            EACH(cls, function(col) {
                                if (find) {
                                    if (col.hidden != TRUE && sf.getEditor(col, r) != _N) {
                                        name = col.name;
                                        return FALSE
                                    }
                                } else {
                                    if (col.name == fname) {
                                        find = TRUE
                                    }
                                }
                            })
                        }
                        if (name) {
                            return {
                                row: row,
                                name: name,
                                record: r
                            }
                        }
                        r = ds.getAt(forward ? ++row : --row);
                        if (forward && !r && !hasAutoAppend && sf.autoappend !== FALSE) {
                            sf.hideEditor();
                            ds.create();
                            r = ds.getAt(row);
                            hasAutoAppend = TRUE
                        }
                        if (updown && r && sf.getEditor(col, r) != _N) {
                            name = fname
                        }
                    }
                }
            }
            return NULL
        },
        showEditorBy: function(dir) {
            var sf = this,
                callback = TRUE,
                ed = sf.findEditorBy(dir),
                ced = sf.currentEditor;
            if (ced) {
                ced = ced.editor
            }
            if (ed) {
                (function(cb) {
                    function _onFetch() {
                        cb();
                        ced.un("fetched", _onFetch)
                    }
                    function _onBlur() {
                        if (sf.isFetching) {
                            ced.on("fetched", _onFetch)
                        } else {
                            cb()
                        }
                        ced.un("blur", _onBlur)
                    }
                    if (ced && ced instanceof A.Lov) {
                        ced.on("blur", _onBlur);
                        sf.hideEditor()
                    } else {
                        sf.hideEditor();
                        cb()
                    }
                })(function() {
                    var row = ed.row,
                        record = ed.record;
                    sf.fireEvent(EVT_CELL_CLICK, sf, row, ed.name, record, callback);
                    sf.fireEvent(EVT_ROW_CLICK, sf, row, record)
                })
            }
        },
        focusRecord: function(record) {
            this.focusRow(record.ds.indexOf(record))
        },
        focusRow: function(row) {
            var r = ROW_HEIGHT + 1,
                ub = this.ub,
                stop = ub.getScroll().top,
                h = ub.getHeight(),
                sh = ub.dom.scrollWidth > ub.dom.clientWidth ? 16 : 0;
            if (row * r < stop) {
                if (ub["scrollToQueue"]) {
                    ub["scrollToQueue"].push(row * r - 1)
                } else {
                    ub["scrollToQueue"] = [];
                    ub["scrollToQueue"].push(row * r - 1)
                }
            } else {
                if ((row + 1) * r > (stop + h - sh)) {
                    if (ub["scrollToQueue"]) {
                        ub["scrollToQueue"].push((row + 1) * r - h + sh)
                    } else {
                        ub["scrollToQueue"] = [];
                        ub["scrollToQueue"].push((row + 1) * r - h + sh)
                    }
                }
            }
            if (!ub["srollTask"]) {
                ub["scrollTaskInfo"] = {
                    run: function() {
                        if (ub["scrollToQueue"]) {
                            var scrollDrift = ub["scrollToQueue"].pop();
                            if (scrollDrift) {
                                ub.scrollTo(TOP, scrollDrift)
                            }
                            ub["scrollToQueue"] = []
                        } else {
                            ub["scrollToQueue"] = []
                        }
                    },
                    interval: 200,
                    scope: this
                };
                ub["srollTask"] = Ext.TaskMgr.start(ub["scrollTaskInfo"])
            }
        },
        focusColumn: function(name) {
            var sf = this,
                r = ROW_HEIGHT + 1,
                ub = sf.ub,
                sleft = ub.getScroll().left,
                ll = 0,
                tw = 0,
                lw = 0,
                lr;
            EACH(sf.columns, function(c) {
                if (c.name == name) {
                    tw = c.width
                }
                if (c.hidden !== TRUE) {
                    if (c.lock === TRUE) {
                        lw += c.width
                    } else {
                        if (tw == 0) {
                            ll += c.width
                        }
                    }
                }
            });
            lr = ll + tw;
            if (ll < sleft) {
                ub.scrollTo(LEFT, ll)
            } else {
                if ((lr - sleft) > (sf.width - lw)) {
                    ub.scrollTo(LEFT, lr - sf.width + lw + (ub.dom.scrollHeight > ub.dom.clientHeight ? 16 : 0))
                }
            }
        },
        hideEditor: function() {
            var sf = this,
                ced = sf.currentEditor;
            if (ced) {
                var ed = ced.editor;
                if (ed) {
                    if (!ed.canHide || ed.canHide()) {
                        ed.el.un(EVT_KEY_DOWN, sf.onEditorKeyDown, sf);
                        ed.un(EVT_SELECT, sf.onEditorSelect, sf);
                        Ext.fly(DOC_EL).un(EVT_MOUSE_DOWN, sf.onEditorBlur, sf);
                        ed.move(-20000);
                        var view = ed.autocompleteview;
                        if (view) {
                            view.hide()
                        }
                        sf.editing = FALSE;
                        ed.blur();
                        ed.onBlur();
                        ed.isFireEvent = FALSE;
                        ed.isHidden = TRUE;
                        if (ed.collapse) {
                            ed.collapse()
                        }
                        delete sf.currentEditor
                    }
                }
            }
        },
        onEditorBlur: function(e, t) {
            var sf = this,
                ced = sf.currentEditor;
            if (ced && !ced.editor.isEventFromComponent(t)) {
                sf.hideEditor.defer($L.isIE ? 10 : 0, sf)
            }
        },
        onLockHeadMove: function(e) {
            var sf = this;
            sf.hmx = e.xy[0] - sf.lht.getXY()[0];
            sf.lh.setStyle(CURSOR, sf.isOverSplitLine(sf.hmx) ? W_RESIZE : DEFAULT)
        },
        onUnLockHeadMove: function(e) {
            var sf = this;
            var uht = sf.uht;
            sf.hmx = e.xy[0] - (uht ? uht.getXY()[0] + uht.getScroll().left : 0) + sf.lockWidth;
            sf.uh.setStyle(CURSOR, sf.isOverSplitLine(sf.hmx) ? W_RESIZE : DEFAULT)
        },
        onHeadMouseDown: function(e) {
            var sf = this;
            sf.dragWidth = -1;
            if (sf.overColIndex == undefined || sf.overColIndex == -1) {
                return
            }
            sf.dragIndex = sf.overColIndex;
            sf.dragStart = e.getXY()[0];
            sf.sp.setHeight(sf.wrap.getHeight()).show().setStyle({
                top: sf.wrap.getXY()[1] + PX,
                left: e.xy[0] + PX
            });
            Ext.get(DOC_EL).on(EVT_MOUSE_MOVE, sf.onHeadMouseMove, sf).on(EVT_MOUSE_UP, sf.onHeadMouseUp, sf);
            e.stopEvent()
        },
        onHeadClick: function(e, t) {
            var sf = this,
                target = Ext.fly(t).parent(TD),
                ds = sf.dataset,
                atype;
            if (target) {
                atype = target.getAttributeNS(_N, ATYPE)
            }
            if (atype == GRID$HEAD) {
                return true;
                var index = target.getAttributeNS(_N, DATA_INDEX),
                    col = sf.findColByName(index);
                if (col && col.sortable === TRUE) {
                    if (ds.isModified() && ds.sorttype == "remote") {
                        A.showInfoMessage("提示", "有未保存数据!");
                        return
                    }
                    var d = target.child(DIV),
                        ot = _N;
                    if (sf.currentSortTarget) {
                        sf.currentSortTarget.removeClass([GRID_ASC, GRID_DESC])
                    }
                    sf.currentSortTarget = d;
                    if (IS_EMPTY(col.sorttype)) {
                        col.sorttype = DESC;
                        d.removeClass(GRID_ASC).addClass(GRID_DESC);
                        ot = DESC
                    } else {
                        if (col.sorttype == DESC) {
                            col.sorttype = ASC;
                            d.removeClass(GRID_DESC).addClass(GRID_ASC);
                            ot = ASC
                        } else {
                            col.sorttype = _N;
                            d.removeClass([GRID_DESC, GRID_ASC])
                        }
                    }
                    ds.sort(index, ot)
                }
            } else {
                if (atype == GRID$ROWCHECK) {
                    var cb = target.child(SELECT_DIV_ATYPE);
                    if (cb) {
                        var checked = cb.hasClass(ITEM_CKB_C);
                        if (!checked) {
                            ds.selectAll()
                        } else {
                            ds.unSelectAll()
                        }
                    }
                }
            }
        },
        setRadioStatus: function(el, checked) {
            if (!checked) {
                el.removeClass(ITEM_RADIO_IMG_C).addClass(ITEM_RADIO_IMG_U)
            } else {
                el.addClass(ITEM_RADIO_IMG_C).removeClass(ITEM_RADIO_IMG_U)
            }
        },
        setCheckBoxStatus: function(el, checked) {
            if (!checked) {
                el.removeClass(ITEM_CKB_C).addClass(ITEM_CKB_U)
            } else {
                el.addClass(ITEM_CKB_C).removeClass(ITEM_CKB_U)
            }
        },
        setSelectDisable: function(el, record) {
            var flag = this.dataset.selected.indexOf(record) == -1;
            if (this.selectionmodel == MULTIPLE) {
                el.removeClass([ITEM_CKB_C, ITEM_CKB_U]).addClass(flag ? ITEM_CKB_READONLY_U : ITEM_CKB_READONLY_C)
            } else {
                el.removeClass([ITEM_RADIO_IMG_C, ITEM_RADIO_IMG_U, ITEM_RADIO_IMG_READONLY_C, ITEM_RADIO_IMG_READONLY_U]).addClass(flag ? ITEM_RADIO_IMG_READONLY_U : ITEM_RADIO_IMG_READONLY_C)
            }
        },
        setSelectEnable: function(el, record) {
            var flag = this.dataset.selected.indexOf(record) == -1;
            if (this.selectionmodel == MULTIPLE) {
                el.removeClass([ITEM_CKB_READONLY_U, ITEM_CKB_READONLY_C]).addClass(flag ? ITEM_CKB_U : ITEM_CKB_C)
            } else {
                el.removeClass([ITEM_RADIO_IMG_U, ITEM_RADIO_IMG_C, ITEM_RADIO_IMG_READONLY_U, ITEM_RADIO_IMG_READONLY_C]).addClass(flag ? ITEM_RADIO_IMG_U : ITEM_RADIO_IMG_C)
            }
        },
        setSelectStatus: function(record) {
            var sf = this,
                ds = sf.dataset;
            if (ds.selectfunction) {
                var cb = Ext.get(sf.id + __ + record.id);
                if (cb) {
                    if (!ds.execSelectFunction(record)) {
                        sf.setSelectDisable(cb, record)
                    } else {
                        sf.setSelectEnable(cb, record)
                    }
                }
            }
        },
        onHeadMouseMove: function(e) {
            var sf = this;
            e.stopEvent();
            sf.dragEnd = e.getXY()[0];
            var move = sf.dragEnd - sf.dragStart,
                c = sf.columns[sf.dragIndex],
                w = c.width + move;
            if (w > 40 && w < sf.width) {
                sf.dragWidth = w;
                sf.sp.setStyle(LEFT, e.xy[0] + PX)
            }
        },
        onHeadMouseUp: function(e) {
            var sf = this;
            Ext.get(DOC_EL).un(EVT_MOUSE_MOVE, sf.onHeadMouseMove, sf).un(EVT_MOUSE_UP, sf.onHeadMouseUp, sf);
            sf.sp.hide();
            if (sf.dragWidth != -1) {
                sf.setColumnSize(sf.columns[sf.dragIndex].name, sf.dragWidth)
            }
            sf.syncScroll()
        },
        findColByName: function(name) {
            var r;
            if (name) {
                EACH(this.columns, function(c) {
                    if (c.name && c.name.toLowerCase() === name.toLowerCase()) {
                        r = c;
                        return FALSE
                    }
                })
            }
            return r
        },
        selectRow: function(row, locate) {
            var sf = this,
                ds = sf.dataset,
                record = ds.getAt(row),
                r = (ds.currentPage - 1) * ds.pagesize + row + 1,
                records = concatGroups(searchRootGroupByRecord(sf.groups, record));
            sf.selectedId = record.id;
            if (sf.selectlockTr) {
                sf.selectlockTr.removeClass(ROW_SELECT)
            }
            if (sf.selectUnlockTr) {
                sf.selectUnlockTr.removeClass(ROW_SELECT)
            }
            sf.selectUnlockTr = sf.ubt && sf.ubt.select(records.map(function(r) {
                return "#" + sf.id + $U + r.id
            }).join(","));
            if (sf.selectUnlockTr) {
                sf.selectUnlockTr.addClass(ROW_SELECT)
            }
            sf.selectlockTr = sf.lbt && sf.lbt.select(records.map(function(r) {
                return "#" + sf.id + $L_copy + r.id
            }).join(","));
            if (sf.selectlockTr) {
                sf.selectlockTr.addClass(ROW_SELECT)
            }
            sf.focusRecord(record);
            if (locate !== FALSE && r != NULL) {
                ds.locate.defer(5, ds, [r, FALSE])
            }
        },
        setColumnSize: function(name, size) {
            var sf = this,
                hth, bth, lw = 0,
                uw = 0;
            EACH(sf.columns, function(c) {
                if (c.name && c.name === name) {
                    if (c.hidden === TRUE) {
                        return
                    }
                    c.width = size;
                    if (c.lock !== TRUE) {
                        hth = sf.uh.child(SELECT_TH_DATAINDEX + name + _K);
                        bth = sf.ub.child(SELECT_TH_DATAINDEX + name + _K)
                    } else {
                        if (sf.lh) {
                            hth = sf.lh.child(SELECT_TH_DATAINDEX + name + _K)
                        }
                        if (sf.lb) {
                            bth = sf.lb.child(SELECT_TH_DATAINDEX + name + _K)
                        }
                    }
                }
                if (c.hidden !== TRUE) {
                    c.lock !== TRUE ? (uw += c.width) : (lw += c.width)
                }
            });
            sf.unlockWidth = uw;
            sf.lockWidth = lw;
            if (hth) {
                hth.setStyle(WIDTH, size + PX)
            }
            if (bth) {
                bth.setStyle(WIDTH, size + PX)
            }
            var mlw = Math.max(sf.width - lw, 0);
            if (sf.fb) {
                sf.fb.child(SELECT_TH_DATAINDEX + name + _K).setStyle(WIDTH, size + PX);
                sf.uf.setStyle(WIDTH, mlw + PX);
                sf.fb.child("table[atype=fb.ubt]").setStyle(WIDTH, uw + PX);
                var lft = sf.fb.child("table[atype=fb.lbt]");
                if (lft) {
                    sf.fb.child("div[atype=grid.lf]").setStyle(WIDTH, (lw - 1) + PX);
                    lft.setStyle(WIDTH, lw + PX)
                }
            }
            if (sf.lc) {
                var lcw = Math.max(lw - 1, 0);
                sf.lc.setStyle({
                    width: lcw + PX,
                    display: lcw == 0 ? NONE : _N
                })
            }
            if (sf.lht) {
                sf.lht.setStyle(WIDTH, lw + PX)
            }
            if (sf.lbt) {
                sf.lbt.setStyle(WIDTH, lw + PX)
            }
            sf.uc.setStyle(WIDTH, mlw + PX);
            sf.uh.setStyle(WIDTH, mlw + PX);
            sf.ub.setStyle(WIDTH, mlw + PX);
            sf.uht.setStyle(WIDTH, uw + PX);
            if (sf.ubt) {
                sf.ubt.setStyle(WIDTH, uw + PX)
            }
            sf.syncSize();
            if (Ext.isChrome) {
                sf.wrap.setStyle({
                    position: "relative"
                });
                (function() {
                    sf.wrap.setStyle({
                        position: ""
                    })
                }).defer(1)
            }
            sf.renderBottomScroll();
        },
        drawFootBar: function(objs) {
            var sf = this;
            if (!sf.fb) {
                return
            }
            EACH([].concat(objs ? objs : sf.columns), function(obj) {
                var col = Ext.isString(obj) ? sf.findColByName(obj) : obj;
                if (col && col.footerrenderer) {
                    var fder = A.getRenderer(col.footerrenderer);
                    if (fder == NULL) {
                        alert(NOT_FOUND + col.footerrenderer + METHOD);
                        return
                    }
                    var name = col.name,
                        v = fder(sf.dataset.data, name);
                    if (!IS_EMPTY(v)) {
                        sf.fb.child(SELECT_TD_DATAINDEX + name + _K).update(v)
                    }
                }
            })
        },
        syncSize: function() {
            var sf = this,
                lw = 0;
            EACH(sf.columns, function(c) {
                if (c.hidden !== TRUE) {
                    if (c.lock === TRUE) {
                        lw += c.width
                    }
                }
            });
            if (lw != 0) {
                var us = sf.width - lw;
                sf.uc.setWidth(us);
                sf.uh.setWidth(us);
                sf.ub.setWidth(us)
            }
            if (!Ext.isIE8) {
                /*$jq("#"+sf.ub.id).getNiceScroll().remove();
                $jq("#"+sf.ub.id).niceScroll({nativeparentscrolling: true});*/
            }
        },
        classfiyColumns: function() {
            var sf = this;
            sf.lockColumns = [], sf.unlockColumns = [];
            sf.lockWidth = 0, sf.unlockWidth = 0;
            EACH(sf.columns, function(c) {
                if (c.lock === TRUE) {
                    sf.lockColumns.add(c);
                    if (c.hidden !== TRUE) {
                        sf.lockWidth += c.width
                    }
                } else {
                    sf.unlockColumns.add(c);
                    if (c.hidden !== TRUE) {
                        sf.unlockWidth += c.width
                    }
                }
            });
            sf.columns = sf.lockColumns.concat(sf.unlockColumns)
        },
        showColumn: function(name) {
            var col = this.findColByName(name);
            if (col && col.visiable != FALSE) {
                if (col.hidden === TRUE) {
                    delete col.hidden;
                    col.forexport = TRUE;
                    this.setColumnSize(name, col.hiddenwidth || col.width);
                    delete col.hiddenwidth;
                    this.wrap.select(SELECT_TD_DATAINDEX + name + _K).show()
                }
            }
            this.renderBottomScroll()
        },
        hideColumn: function(name) {
            var col = this.findColByName(name);
            if (col && col.visiable != FALSE) {
                if (col.hidden !== TRUE) {
                    col.hiddenwidth = col.width;
                    this.setColumnSize(name, 0, FALSE);
                    this.wrap.select(SELECT_TD_DATAINDEX + name + _K).hide();
                    col.hidden = TRUE;
                    col.forexport = FALSE
                }
            }
            this.renderBottomScroll()
        },
        removeColumn: function(name) {
            var sf = this,
                cols = sf.columns,
                lockNames = [],
                unLockNames = [];
            EACH(name, function(n) {
                col = sf.findColByName(n);
                if (!col) {
                    return
                }
                if (col.lock) {
                    lockNames.push(n)
                } else {
                    unLockNames.push(n)
                }
                cols.splice(cols.indexOf(col), 1)
            });
            var lnl = lockNames.length,
                unl = unLockNames.length,
                selector = [];
            if (lnl || unl) {
                sf.classfiyColumns();
                if (lnl) {
                    var lw = sf.lockWidth,
                        ucw = sf.wrap.getWidth() - lw;
                    for (var i = 0; i < lnl; i++) {
                        selector.push(SELECT_DATAINDEX + lockNames[i] + _K)
                    }
                    if (lw) {
                        sf.lht.setWidth(lw);
                        sf.lc.setWidth(lw);
                        sf.lbt.dom.width = lw
                    } else {
                        sf.lc.remove()
                    }
                    sf.uc.setWidth(ucw);
                    sf.uh.setWidth(ucw);
                    sf.ub.setWidth(ucw)
                }
                for (var i = 0; i < unl; i++) {
                    selector.push(SELECT_DATAINDEX + unLockNames[i] + _K)
                }
                sf.wrap.select(selector.join(",")).remove();
                var ulw = sf.unlockWidth;
                sf.uht.setWidth(ulw);
                sf.ubt.setWidth(ulw)
            }
        },
        createHead: Ext.isIE || Ext.isIE9 ?
            function(cols, method, name, parent, index) {
                var trs = parent.query(TR),
                    t = Ext.fly(trs[0]).child("th:last()"),
                    tds;
                if (name) {
                    tds = parent.query(SELECT_DATAINDEX + name + _K)[0]
                }
                if (method == INSERT_AFTER) {
                    tds = tds.nextSibling || NULL;
                    index++
                } else {
                    if (method == APPEND) {
                        if (t) {
                            tds = t.dom
                        }
                        index = -1
                    }
                }
                EACH(cols, function(c) {
                    var th = Ext.get(DOC.createElement(TH)),
                        td = Ext.get(trs[1].insertCell(index)),
                        w = c.width,
                        n = c.name;
                    if (index > -1) {
                        index++
                    }
                    td.addClass("grid-hc").set({
                        dataindex: n,
                        atype: GRID$HEAD
                    }).update("<div>" + c.prompt + "</div>");
                    if (tds) {
                        trs[0].insertBefore(th.dom, tds)
                    } else {
                        trs[0].appendChild(th.dom)
                    }
                    th.setStyle(WIDTH, w + PX).set({
                        dataindex: n
                    })
                })
            } : function(cols, method, name, parent) {
                var html = [],
                    html2 = [],
                    tds = parent.query(method != APPEND ? SELECT_DATAINDEX + name + _K : TR),
                    th = Ext.fly(tds[0]).child("th:last()");
                EACH(cols, function(c) {
                    html.push('<th style="width:', c.width, PX, ';" ', DATA_INDEX, '="', c.name, '"></th>');
                    html2.push('<td class="grid-hc" atype="grid.head" ', DATA_INDEX, '="', c.name, '"><div>', c.prompt, "</div></td>")
                });
                new Ext.Template(html.join(_N))[method](tds[0], {});
                new Ext.Template(html2.join(_N))[method](tds[1], {});
                if (th) {
                    th.appendTo(Ext.fly(tds[0]))
                }
            },
        addColumn: function(options, name, where) {
            var sf = this;
            if (sf.dataset.isModified()) {
                A.showInfoMessage(_lang["grid.info"], _lang["grid.info.continue"])
            } else {
                var cols = sf.columns,
                    index = cols.length,
                    oldLock, oldIndex;
                if (name && where) {
                    var column = sf.findColByName(name);
                    if (column) {
                        oldLock = column.lock;
                        oldIndex = sf[oldLock ? "lockColumns" : "unlockColumns"].indexOf(column);
                        index = (where == BEFORE ? 0 : 1) + cols.indexOf(column)
                    } else {
                        alert("未找到列" + name);
                        return
                    }
                }
                var lockCols = [],
                    unLockCols = [];
                EACH(options, function(c) {
                    var opt = Ext.apply(Ext.apply({}, defaultColumnOptions), c),
                        col = sf.findColByName(opt.name);
                    if (col) {
                        return
                    }
                    if (opt.lock) {
                        lockCols.push(opt)
                    } else {
                        unLockCols.push(opt)
                    }
                });
                var newCols = lockCols.concat(unLockCols);
                if (newCols.length) {
                    var method = where ? (where == BEFORE ? INSERT_BEFORE : INSERT_AFTER) : APPEND,
                        lmethod = umethod = method,
                        lname = uname = name,
                        wp = sf.wrap;
                    if (oldLock) {
                        umethod = INSERT_BEFORE;
                        uname = sf.unlockColumns[0].name
                    } else {
                        lmethod = APPEND;
                        lname = NULL
                    }
                    sf.columns = cols.slice(0, index).concat(newCols).concat(cols.slice(index));
                    sf.classfiyColumns();
                    if (lockCols.length) {
                        if (!sf.lht) {
                            sf.lc = new Ext.Template("<DIV class='grid-la' atype='grid.lc' style='width:24px;'><DIV class='grid-lh' atype='grid.lh' unselectable='on' style='height:25px;'><TABLE cellSpacing='0' atype='grid.lht' cellPadding='0' border='0' style='width:25px'><TBODY><TR class='grid-hl'></TR><TR height=25></TR></TBODY></TABLE></DIV><DIV class='grid-lb' atype='grid.lb' style='width:100%;height:255px'></DIV></DIV>").insertFirst(sf.wrap.dom, {}, TRUE);
                            sf.lh = wp.child("div[atype=grid.lh]");
                            sf.lb = wp.child("div[atype=grid.lb]");
                            sf.lht = wp.child("table[atype=grid.lht]");
                            sf.lb[ON](EVT_CLICK, sf.onClick, sf)[ON](EVT_DBLCLICK, sf.onDblclick, sf);
                            sf.lht[ON](EVT_MOUSE_MOVE, sf.onLockHeadMove, sf);
                            sf.lh[ON](EVT_MOUSE_DOWN, sf.onHeadMouseDown, sf)[ON](EVT_CLICK, sf.onHeadClick, sf)
                        }
                        sf.createHead(lockCols, lmethod, lname, sf.lht, oldIndex)
                    }
                    if (unLockCols.length) {
                        sf.createHead(unLockCols, umethod, uname, sf.uht, oldIndex)
                    }
                    if (sf.lb) {
                        sf.lb.update(_N)
                    }
                    sf.ub.update(_N);
                    EACH(newCols, function(nc) {
                        sf.setColumnSize(nc.name, nc.width)
                    });
                    sf.dataset.query()
                }
            }
        },
        insertColumnBefore: function(name, options) {
            this.addColumn(options, name, BEFORE)
        },
        insertColumnAfter: function(name, options) {
            this.addColumn(options, name, 1)
        },
        setWidth: function(w) {
            var sf = this;
            if (sf.width == w) {
                return
            }
            sf.width = w;
            sf.wrap.setWidth(w + 2);
            var tb = A.CmpManager.get(sf.id + TB);
            if (tb) {
                tb.setWidth(w)
            }
            var nb = A.CmpManager.get(sf.id + NAVBAR);
            if (nb) {
                nb.setWidth(w)
            }
            //导航栏跟底部导航栏同时存在时，会导致toolbar宽度减半 add by lrd
            /*if (tb && nb) {
                tb.setWidth(w / 2);
                nb.setWidth(w / 2)
            }*/
            if (sf.fb) {
                sf.fb.setWidth(w)
            }
            var bw = w - sf.lockWidth;
            sf.uc.setWidth(bw);
            sf.uh.setWidth(bw);
            sf.ub.setWidth(bw);
            if (sf.uf) {
                sf.uf.setWidth(bw)
            }
            sf.renderBottomScroll();
        },
        setHeight: function(h) {
            var sf = this;
            if (sf.height == h) {
                return
            }
            A.Masker.unmask(sf.wb);
            sf.height = h;
            var tb = A.CmpManager.get(sf.id + TB);
            if (tb) {
                h -= ROW_HEIGHT_TB
            }
            var nb = A.CmpManager.get(sf.id + NAVBAR);
            if (nb) {
                h -= ROW_HEIGHT_fb
            }
            if (sf.fb) {
                h -= ROW_HEIGHT
            }
            sf.wrap.setHeight(h);
            var bh = h - ROW_HEIGHT;
            if (sf.lb) {
                sf.lb.setHeight(bh);
                var bmh = sf.lb.getStyle("max-height");
                if (bh > parseInt(bmh)) {
                    bmh = bh + "px";
                    sf.lb.setStyle("max-height", bmh)
                }
            }
            sf.ub.setHeight(bh);
            if (Ext.isChrome && !Ext.isIE8) {
                /*$jq("#"+sf.ub.id).getNiceScroll().remove();
                $jq("#"+sf.ub.id).niceScroll({
                    nativeparentscrolling: true
                });*/
            }
        },
        deleteSelectRows: function(win) {
            var ds = this.dataset,
                selected = [].concat(ds.getSelected());
            if (selected.length > 0) {
                ds.remove(selected)
            }
        },
        remove: function() {
            var selected = this.dataset.getSelected();
            if (selected.length > 0) {
                A.showConfirm(_lang["grid.remove.confirm"], _lang["grid.remove.confirmMsg"], this.deleteSelectRows.createDelegate(this))
            } else {
                A.showInfoMessage(_lang["grid.info"], _lang["grid.remove.select"])
            }
        },
        clear: function(opt) {
            var ds = this.dataset,
                selected = ds.getSelected();
            if(selected.length > 0){
                while (selected[0]) {
                    ds.removeLocal(selected[0])
                }
            }else{
                if(opt && opt.showMessage == false){
                    return;
                }else {
                    A.showInfoMessage(_lang["grid.info"], _lang["grid.remove.select"]);
                }
            }

        },
        _export: function(type, filename, separator, exportParam) {
            this.exportOptions = Ext.apply({
                type: "xls",
                filename: "excel"
            }, arguments.length == 1 ? type : {
                type: type || "xls",
                filename: filename || "excel",
                separator: separator,
                param: exportParam
            });
            this.showExportConfirm()
        },
        showExportConfirm: function() {
            var sf = this,
                n = 0,
                id = sf.id + "_export",
                msg = ['<div class="item-export-wrap" style="margin:10px;width:270px;position:relative;" id="', id, '">', '<div class="grid-uh" atype="grid.uh" style="width: 270px; -moz-user-select: none; text-align: left; height: 25px; cursor: default;" unselectable="on">', '<table cellSpacing="0" cellPadding="0" border="0"><tbody><tr height="25px">', '<td class="export-hc" style="width:22px;" atype="export.rowcheck"><center><div title="', _lang["grid.export.selectinfo"], '" atype="export.headcheck" class="', GRID_CKB, ITEM_CKB_U, '"></div></center></td>', '<td class="export-hc" style="width:222px;" atype="grid-type">', _lang["grid.export.column"], "</td>", "</tr></tbody></table></div>", '<div class="exportBox" style="overflow:auto;height:200px;"><div class="exportNiceScroll"><table cellSpacing="0" cellPadding="0" border="0"><tbody>'],
                exportall = TRUE,
                height = 370,
                exportOptions = sf.exportOptions || (sf.exportOptions = {}),
                type = exportOptions && exportOptions.type;
            EACH(sf.columns, function(c, i) {
                if (!sf.isFunctionCol(c.type)) {
                    if (exportall) {
                        exportall = c.forexport !== FALSE
                    }
                    msg.push("<tr", (n + i) % 2 == 0 ? _N : ' class="', ROW_ALT, '"', '><td class="', GRID_ROWBOX, '" style="width:22px;" ', RECORD_ID, '="', i, '" atype="export.rowcheck"><center><div id="', sf.id, __, i, '" class="', GRID_CKB, c.forexport === FALSE ? ITEM_CKB_U : ITEM_CKB_C, '"></div></center></td><td style="width:222px"><div class="', GRID_CELL, '">', c.prompt, c.hidden ? ['<div style="float:right;color:red">&lt;', _lang["grid.export.hidecolumn"], "&gt;</div>"].join("") : _N, "</div></td></tr>")
                } else {
                    n++
                }
            });
            if (exportall) {
                msg[9] = ITEM_CKB_C
            }
            msg.push("</tbody></table></div></div></div>");
            if (type == "xls" || type == "xlsx") {
                height += ROW_HEIGHT;
                msg.push('<div class="item-radio" class="item-radio" style="margin:15px;width:270px;height:30px">', '<div class="item-radio-option" style="width:128px;float:left" itemvalue="xls">', '<div class="item-radio-img  item-radio-img-', type == "xls" ? "c" : "u", '"></div>', '<label class="item-radio-lb">excel2003</label>', "</div>", '<div class="item-radio-option" style="width:128px;float:left" itemvalue="xlsx">', '<div class="item-radio-img  item-radio-img-', type == "xlsx" ? "c" : "u", '"></div>', '<label class="item-radio-lb">excel2007</label>', "</div>", "</div>")
            }
            msg.push('<div style="margin:15px;width:270px;color:red">', _lang["grid.export.confirmMsg"], "</div>");
            sf.exportwindow = A.showOkCancelWindow(_lang["grid.export.config"], msg.join(_N), function(win2) {
                sf.doExport();
                win2.body.un(EVT_CLICK, sf.onExportClick, sf)
            }, NULL, 380, height);
            sf.exportwindow.body.on(EVT_CLICK, sf.onExportClick, sf);
            if (!Ext.isIE8) {
                /*$jq(".exportBox").niceScroll(".exportNiceScroll")*/
            }
        },
        onExportClick: function(e, t) {
            t = Ext.fly(t);
            var sf = this,
                rowbox = t.parent("td.grid-rowbox") || t.parent("td.export-hc"),
                radio = t.hasClass("item-radio-option") ? t : t.parent("div.item-radio-option");
            if (rowbox) {
                var atype = rowbox.getAttributeNS(_N, ATYPE);
                if (atype == "export.rowcheck") {
                    var rid = rowbox.getAttributeNS(_N, RECORD_ID),
                        cb = rowbox.child(DIV),
                        checked = cb.hasClass(ITEM_CKB_C),
                        _atype = cb.getAttributeNS(_N, ATYPE),
                        cols = sf.columns;
                    sf.setCheckBoxStatus(cb, !checked);
                    if (_atype == "export.headcheck") {
                        var che = (sf.isFunctionCol(cols[0].type) ? 1 : 0) + (sf.isFunctionCol(cols[1].type) ? 1 : 0),
                            ctrl = e.ctrlKey;
                        sf.exportwindow.body.select("td[atype=export.rowcheck] div[atype!=export.headcheck]").each(function(cbs, o, i) {
                            o = cols[i + che];
                            if (!ctrl || !o.hidden) {
                                sf.setCheckBoxStatus(cbs, !checked);
                                o.forexport = !checked
                            }
                        })
                    } else {
                        cols[rid].forexport = !checked
                    }
                }
            } else {
                if (radio) {
                    sf.setRadioStatus(radio.child("div"), TRUE);
                    sf.setRadioStatus((radio.prev() || radio.next()).child("div"), FALSE);
                    sf.exportOptions.type = radio.getAttributeNS(_N, "itemvalue")
                }
            }
        },
        doExport: function() {
            var sf = this,
                opt = sf.exportOptions || {};
            A.doExport(sf.dataset, sf.columns, NULL, opt.type, opt.separator, opt.filename, opt.param);
            delete sf.exportOptions
        },
        destroy: function() {
            A.Grid.superclass.destroy.call(this);
            this.processDataSetLiestener("un");
            this.sp.remove();
            delete this.sp
        },
        createCompositeEditor: function(name, colspan, record, obj) {
            var sf = this,
                id = sf.id,
                ds = sf.dataset,
                rid = record.id,
                col = sf.findColByName(name),
                lock = col.lock,
                crow = Ext.get(id + (lock ? $L_copy : $U) + rid),
                trow = Ext.get(id + (lock ? $U : $L_copy) + rid),
                bt = lock ? sf.lbt : sf.ubt,
                row = this.dataset.indexOf(record);
            if (sf.currentEditor && sf.currentEditor.editor instanceof CheckBox) {
                sf.hideEditor()
            }
            var table = bt.dom,
                ltb = table.tBodies[0],
                ltr = document.createElement(TR),
                td = document.createElement(TD);
            ltr.id = id + "_cmp_" + name + _ + rid;
            Ext.fly(ltr).addClass(row % 2 == 0 ? _N : ROW_ALT);
            Ext.fly(ltr).set({
                _row: record.id
            });
            td.colSpan = colspan;
            td.innerHTML = obj.html;
            ltr.appendChild(td);
            if (ds.indexOf(record) >= ds.data.length - 1) {
                ltb.appendChild(ltr)
            } else {
                var nr = ds.getAt(ds.indexOf(record) + 1);
                var tr = Ext.get(id + (lock ? $L_copy : $U) + nr.id);
                ltb.insertBefore(ltr, tr.dom)
            }
            if (trow) {
                trow.setHeight(obj.height)
            }
            Ext.each(crow.dom.childNodes, function(c) {
                var di = Ext.get(c).getAttributeNS(_N, DATA_INDEX);
                if (di == name) {
                    return FALSE
                }
                c.rowSpan = 2
            });
            this.hideEditor()
        },
        createCompositeRow: function(name, map, obj, colspan, record, columns) {
            var html = [];
            map.name = name;
            if (columns.find("name", name)) {
                var row = this.dataset.indexOf(record);
                html.push('<tr id="', this.id, "_cmp_", name, _, record.id, '" class="' + (row % 2 == 0 ? _N : ROW_ALT) + '" _row="' + record.id + '"><td colSpan="', colspan, '">');
                html.push(obj.html);
                html.push("</td></tr>");
                map.html = html.join("")
            } else {
                map.height = obj.height
            }
        },
        removeCompositeEditor: function(name, record) {
            var sf = this,
                id = sf.id,
                rid = record.id,
                col = sf.findColByName(name),
                lock = col.lock,
                crow = Ext.get(id + (lock ? $L_copy : $U) + rid),
                trow = Ext.get(id + (lock ? $U : $L_copy) + rid);
            if (sf.currentEditor && sf.currentEditor.editor instanceof CheckBox) {
                sf.hideEditor()
            }
            if (trow) {
                trow.setHeight(22)
            }
            Ext.each(crow && crow.dom.childNodes, function(c) {
                if (Ext.get(c).getAttributeNS(_N, DATA_INDEX) == name) {
                    return FALSE
                }
                c.rowSpan = 1
            });
            var d = Ext.get(id + "_cmp_" + name + _ + rid);
            if (d) {
                d.remove()
            }
        },
        show: function() {
            A.Grid.superclass.show.call(this);
            this.wb.show()
        },
        hide: function() {
            A.Grid.superclass.hide.call(this);
            this.wb.hide()
        },
        getContext: function() {
            var location = window.document.location,
                pathName = location.pathname,
                projectName = pathName.substring(0, pathName.substr(1).indexOf("/") + 2);
            return projectName
        },
        spreadSheet: function() {
            this.showSpreadSheetWindow()
        },
        showSpreadSheetWindow: function() {
            var sf = this,
                ctx = sf.getContext(),
                bind_id = sf.id,
                id = bind_id + "_spreadSheet_div",
                url = ctx + "sys_spreadSheet.lview?bind_id=" + bind_id;
            sf.isWinOpen = true;
            sf.win = new A.Window({
                id: id,
                title: sf.title || "快速表格",
                url: Ext.urlAppend(url),
                width: 1000,
                height: 600,
                fullScreen: true
            });
            sf.win.on("close", sf.onWinClose, sf)
        }
    });
    A.Grid.revision = "$Rev$"
})($L);