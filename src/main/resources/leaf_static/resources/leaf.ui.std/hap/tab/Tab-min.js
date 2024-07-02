(function(A){
    var _N = '',
        sd='scroll-disabled',
        tslo='tab-scroll-left-over',
        tsro='tab-scroll-right-over',
        tsl='tab-scroll-left',
        tsr='tab-scroll-right',
        tc='tab-close',
        tbo='tab-btn-over',
        tbd='tab-btn-down',
        ts='tab-scroll',
        RIGHT = 'right',
        LEFT = 'left',
        ACTIVE = 'active',
        UNACTIVE = 'unactive',
        NONE = 'none',
        BLOCK = 'block',
        EVT_SELECT = 'select',
        EVT_BEFORE_OPEN = 'beforeopen',
        EVT_BEFORE_SELECT = 'beforeselect',
        PADDING_LEFT = 'padding-left',
        STRIP = 'strip',
        $STRIP = '.'+STRIP,
        ERROR = '销毁Tab出错: ',
        STRIP_TPL=['<div class="strip unactive"  unselectable="on">',
            '<div class="strip-left"></div>',
            '<div style="width:{stripwidth}px;" class="strip-center"><div class="tab-close"></div>{prompt}</div>',
            '<div class="strip-right"></div>',
            '</div>'],
        //BODY_TPL='<div  hideFocus style="height:{bodyheight}px" class="tab"></div>';
        BODY_TPL='<div  hideFocus style="/*height:{bodyheight}px*/" class="tab"></div>';
    //IFRAME_TPL='<iframe frameborder="0" style="border:none;width:{bodywidth}px;height:{bodyheight}px;left:-10000px;top:-10000px;" class="tab"></iframe>';
    IFRAME_TPL='<iframe frameborder="0" style="border:none;width:{bodywidth}px;" class="tab"></iframe>';

    /**
     * @class Leaf.Tab
     * @extends Leaf.Component
     * Tab组件.
     * @author njq.niu@hand-china.com
     * @constructor
     * @param {Object} config 配置对象.
     */
    A.Tab = Ext.extend(A.Component,{
        constructor: function(config){
//      this.intervalIds=[];
            A.Tab.superclass.constructor.call(this,config);
        },
        initComponent:function(config){
            A.Tab.superclass.initComponent.call(this, config);
            var sf = this,
                w = sf.wrap,
                h = sf.head = w.child('div[atype=tab.strips]'),
                s = sf.stripwrap = h.parent();
            sf.stripwidth = config.stripwidth||60;
            sf.body = w.child('div.item-tab-body');
            sf.scrollLeft = w.child('div[atype=scroll-left]');
            sf.scrollRight = w.child('div[atype=scroll-right]');
            sf.sp = s.parent();
            // $jq('div.tab').niceScroll();
            // $jq("#" + sf.id).find("div.item-tab-body.tab").niceScroll();
            sf.selectTab(config.selected||0);
        },
        processListener: function(ou){
            var sf = this;
            A.Tab.superclass.processListener.call(sf,ou);
            sf.sp[ou]('mousedown',sf.onMouseDown, sf)
                [ou]('mouseup',sf.onMouseUp, sf);
            sf.stripwrap[ou]('click',sf.onClick, sf)
                [ou]('mousewheel',sf.onMouseWheel, sf);
        },
        initEvents:function(){
            A.Tab.superclass.initEvents.call(this);
            this.addEvents(
                /**
                 * @event select
                 * 选择事件.
                 * @param {Leaf.TabPanel} tabPanel TabPanel对象.
                 * @param {Number} index 序号.
                 * @param {Leaf.Tab} tab Tab对象.
                 */
                EVT_SELECT,
                /**
                 * @event beforeopen
                 * 选择事件.
                 * @param {Leaf.TabPanel} tabPanel TabPanel对象.
                 * @param {Number} index 序号.
                 */
                EVT_BEFORE_OPEN,
                /**
                 * @event beforeselect
                 * 选择之前事件.
                 * @param {Leaf.TabPanel} tabPanel TabPanel对象.
                 * @param {Number} index 序号.
                 * @param {Leaf.Tab} tab Tab对象.
                 */
                EVT_BEFORE_SELECT
            );

        },
        /**
         * 改变某个Tab页的url。当是当前tab页时，会立即重新加载，否则会在选中之后加载。
         * @param {Number} index TabItem序号。当index<0时，TabItem序号等于TabItem的个数加上index。
         * @param {String} url 地址。
         */
        setURL:function(index,url){
            var sf = this,
                tab = sf.getTab(index);
            if(tab){
                index = tab.index;
                if(index == sf.selectedIndex){
                    sf.reloadTab(index,url);
                }else{
                    tab.body.loaded = false;
                    sf.items[index].ref = url;
                }
            }
        },
        /**
         * 重新设置tab大小
         * @param {Number} index TabItem序号
         */
        resizeTab:function(index){
            var sf = this,tab=sf.getTab(index);
            if(!tab)return;
            var cmps = tab.body.cmps;
            var newTabHeight=20;
            var parentTabHeight = 100;
            var formIds={};
            var parentTabId='';
            var hasParentTab = false;
            Leaf.onReady(function () {
                try{
                    for(var cmpKey in cmps){
                        var jqObject= $jq('#'+cmpKey);
                        if(jqObject.parent().closest('.item-tab').length>0&&jqObject.parent().closest('.item-tab').parent().closest('.item-tab').length>0){
                            hasParentTab=true;
                        }
                        var addHeight = 0;
                        try{
                            if(jqObject.hasClass('item-grid-wrap')||$(cmpKey) instanceof Leaf.Grid){
                                var parentTable = $jq('#'+cmpKey+'_wrap').parent().closest('table');
                                if(!parentTable.hasClass('layout-form')){
                                    if($jq('#'+cmpKey+'_wrap').parent().closest('table').hasClass('layout-table')){
                                        if(hasParentTab){
                                            addHeight = parentTable.height() + 30;
                                        }else{
                                            addHeight = parentTable.height() + 20;
                                        }
                                    }else{
                                        if(hasParentTab){
                                            addHeight =  $jq('#'+cmpKey+'_wrap').height()+30;
                                        }else{
                                            addHeight =  $jq('#'+cmpKey+'_wrap').height()+20;
                                        }
                                    }
                                    if(hasParentTab){
                                        parentTabHeight = parentTabHeight + addHeight;
                                        parentTabId = jqObject.parent().closest('.item-tab').parent().closest('.item-tab')[0].id;
                                    }
                                }
                            }else if($(cmpKey) instanceof Leaf.Field||$(cmpKey) instanceof Leaf.CheckBox){
                                if(jqObject.closest('table').hasClass('layout-form')){
                                    if(!formIds[jqObject.closest('table')[0].id]){
                                        addHeight = jqObject.closest('table').height()+20;
                                        formIds[jqObject.closest('table')[0].id] = true;
                                    }
                                }else if((jqObject.closest('table').hasClass('layout-table')||jqObject.closest('table').hasClass('item-fieldset-body'))&&!jqObject.closest('table').hasClass('layout-form')){
                                    var parentTable = jqObject.closest('table').parent().closest('table');
                                    if(parentTable.hasClass('layout-form')){
                                        if(!formIds[parentTable[0].id]){
                                            addHeight = parentTable.height()+20;
                                            formIds[parentTable[0].id] = true;
                                            if(hasParentTab){
                                                parentTabHeight = parentTabHeight + addHeight;
                                                parentTabId = jqObject.parent().closest('.item-tab').parent().closest('.item-tab')[0].id;
                                            }
                                        }
                                    }
                                }
                            }else if($(cmpKey) instanceof Leaf.Tab){
                                addHeight =  $(cmpKey).height+100;
                                if(hasParentTab){
                                    parentTabHeight = parentTabHeight + addHeight;
                                    parentTabId = jqObject.parent().closest('.item-tab').parent().closest('.item-tab')[0].id;
                                }
                            } else if(jqObject.hasClass('item-gridbox-wrap')||$(cmpKey) instanceof Leaf.GridBox){
                                addHeight = $jq('#'+cmpKey).height();
                                if(hasParentTab){
                                    parentTabHeight = parentTabHeight + addHeight;
                                    parentTabId = $jq('#'+cmpKey).parent().closest('.item-tab').parent().closest('.item-tab')[0].id;
                                }
                            }else{
                                if($(cmpKey).height){
                                    addHeight = $(cmpKey).height+15;
                                }
                                if(hasParentTab){
                                    parentTabHeight = parentTabHeight + addHeight;
                                    parentTabId = jqObject.parent().closest('.item-tab').parent().closest('.item-tab')[0].id;
                                }
                            }
                        }catch (e) {
                            console.log(e);
                        }
                        newTabHeight = newTabHeight + addHeight;
                    }
                }catch (e) {
                    console.log(e);
                }
                if(newTabHeight>20){
                    $(sf.id).setHeight(newTabHeight);
                }
                if(parentTabHeight>100 && parentTabId != ""){
                    $(parentTabId).setHeight(parentTabHeight);
                }
            });
        },
        /**
         * 选中某个Tab页
         * @param {Number} index TabItem序号。当index<0时，TabItem序号等于TabItem的个数加上index。
         */
        selectTab:function(index,needRefresh){
            var sf = this,tab=sf.getTab(index);
            if(!tab)return;
            var index=tab.index,
                activeStrip = tab.strip,
                activeBody = tab.body;
            if(activeStrip.hasClass(sd)){
                sf.selectTab(index+1);
                return;
            }
            if(sf.fireEvent(EVT_BEFORE_SELECT,sf,index,tab)){
                sf.selectedIndex=index;
                if(sf.activeTab)sf.activeTab.replaceClass(ACTIVE,UNACTIVE);
                sf.activeTab = activeStrip;
                activeStrip.replaceClass(UNACTIVE,ACTIVE);
                var stripwrap = sf.stripwrap,
                    scrollLeft = sf.scrollLeft,
                    scrollRight = sf.scrollRight,
                    l=activeStrip.dom.offsetLeft,w=activeStrip.getWidth(),
                    sl=stripwrap.getScroll().left,sw=stripwrap.getWidth(),hw=sf.head.getWidth();
                tr=l+w-sl-sw,tl=sl-l;
                if(tr>0){
                    scrollRight.removeClass(sd);
                    scrollLeft.removeClass(sd);
                    stripwrap.scrollTo(LEFT,sl+tr);
                }else if(tl>0){
                    scrollLeft.removeClass(sd);
                    stripwrap.scrollTo(LEFT,sl-tl);
                    scrollRight.removeClass(sd);
                }
                if(sw+stripwrap.getScroll().left>=hw){
                    stripwrap.scrollTo(LEFT,hw-sw);
                    scrollRight.addClass(sd);
                }else if(index==0){
                    stripwrap.scrollTo(LEFT,0);
                    scrollLeft.addClass(sd);
                }
                if(activeBody){
                    if(sf.activeBody){
                        // $jq("#" + sf.id).find(".tab").getNiceScroll().remove();
                        sf.activeBody.setStyle({left:'-10000px',top:'-10000px'});
                        // sf.activeBody.setStyle({display:'none'});
                    }
                    sf.activeBody = activeBody.setStyle({left:0,top:0,width:'inherit'});
                    // sf.activeBody = activeBody.setStyle({display:'block'});
                }
                if(sf.items[index].ref && !activeBody.loading && (activeBody.loaded!= true||needRefresh)){
                    activeBody.loading = true;
                    sf.load(sf.items[index].ref,activeBody,index);
                }else{
                    sf.fireEvent(EVT_SELECT, sf, index,tab);
                }
            }
            sf.resizeTab(index);
            // $jq(".tab.div.grid-ub").getNiceScroll().resize();
            // $jq("#"+sf.id).find(".tab").getNiceScroll().resize();
            // $jq("#" + sf.id).find(".tab").niceScroll({nativeparentscrolling:false});
            // $jq("#" + sf.id).find(".tab").getNiceScroll().resize();
            // $jq('div.tab').getNiceScroll().resize();
        },
        /**
         * 打开一个指定引用地址的Tab页，如果该指定的引用地址的页面已经被打开，则选中该Tab页
         * @param {String} ref Tab页面的引用地址
         * @param {String} prompt Tab的标题
         */
        openTab : function(ref,prompt){
            var sf = this,i=0,
                items = sf.items,l = items.length;
            for(;i<l;i++){
                var item = items[i];
                if(item.ref&&item.ref==ref){
                    sf.selectTab(i);return;
                }
            }
            if(sf.fireEvent(EVT_BEFORE_OPEN,sf,l)!==false){
                items.push({'ref':ref,prompt:prompt});
                var stripwidth=Math.max(A.TextMetrics.measure(document.body,prompt).width+30,sf.stripwidth),
                    head = sf.head,
                    body = sf.body,
                    stripwrap = sf.stripwrap,
                    width = head.getWidth()+stripwidth+20;
                head.setWidth(width);
                if(width>stripwrap.getWidth()){
                    sf.scrollLeft.setStyle({display:BLOCK});
                    sf.scrollRight.setStyle({display:BLOCK});
                    stripwrap.setStyle(PADDING_LEFT,'1px');
                }
                new Ext.Template(STRIP_TPL).append(head.dom,{'prompt':prompt,'stripwidth':stripwidth});
//          new Ext.Template(BODY_TPL).append(body.dom,{'bodyheight':body.getHeight()});
//          new Ext.Template(sf.loadtype == 'iframe' ? IFRAME_TPL : BODY_TPL).append(body.dom,{id:sf.id,'bodywidth':body.getWidth()-body.getBorderWidth('lr'),'bodyheight':body.getHeight()-body.getBorderWidth('tb')});
                new Ext.Template(sf.loadtype == 'iframe' ? IFRAME_TPL : BODY_TPL).append(body.dom,{id:sf.id,'bodywidth':body.getWidth()-body.getBorderWidth('lr')});

                sf.selectTab(l);
            }
        },
        /**
         * 关闭某个Tab页
         * @param {Integer} index TabItem序号。当index<0时，TabItem序号等于TabItem的个数加上index。
         */
        closeTab : function(o){
            var sf = this,tab=sf.getTab(o);
            if(!tab)return;
            var strip=tab.strip,body=tab.body,index=tab.index;
            if(!strip.child('div.'+tc)){
                A.showWarningMessage('警告','该Tab页无法被关闭!')
                return;
            }
            if(sf.activeBody == tab.body){
                sf.activeBody=null;
                sf.activeTab=null;
            }
            sf.items.splice(index,1);
            var head = sf.head,
                stripwrap = sf.stripwrap,
                width= head.getWidth()-strip.getWidth();
//      head.setWidth(width);//IE8慢
            if(width <= stripwrap.getWidth()){
                sf.scrollLeft.setStyle({display:NONE});
                sf.scrollRight.setStyle({display:NONE});
                stripwrap.setStyle(PADDING_LEFT,'0');
            }
            strip.remove();
            body.remove();

            delete body.loaded;
            (function(){
                Ext.iterate(body.cmps,function(key,cmp){
                    if(cmp.destroy){
                        try{
                            cmp.destroy();
                        }catch(e){
                            alert(ERROR + e)
                        }
                    }
                });
                head.setWidth(width);
            }).defer(10);
            if(index==sf.selectedIndex)sf.selectTab(index-1);
            else if(index<sf.selectedIndex)sf.selectedIndex--;
        },
        destroy : function(){
//      var bodys = Ext.DomQuery.select('div.tab',this.body.dom);
            Ext.each(this.body.dom.children,function(body){
                Ext.iterate(Ext.get(body).cmps,function(key,cmp){
                    if(cmp.destroy){
                        try{
                            cmp.destroy();
                        }catch(e){
                            alert(ERROR + e)
                        }
                    }
                });
            });
            A.Tab.superclass.destroy.call(this);
        },
        /**
         * 将某个Tab页设为不可用。当TabItem有且仅有1个时，该方法无效果。
         * @param {Integer} index TabItem序号。当index<0时，TabItem序号等于TabItem的个数加上index。
         */
        setDisabled : function(index){
            var sf = this,tab = sf.getTab(index);
            if(!tab)return;
            if(sf.items.length > 1){
                var strip = tab.strip,index = tab.index;
                if(sf.activeTab==strip){
                    sf.selectTab(index+(sf.getTab(index+1)?1:-1))
                }
                strip.addClass(sd);
            }
        },
        /**
         * 将某个Tab页设为可用
         * @param {Integer} index TabItem序号。当index<0时，TabItem序号等于TabItem的个数加上index。
         */
        setEnabled : function(index){
            var tab = this.getTab(index);
            if(!tab)return;
            tab.strip.removeClass(sd);
        },
        getTab : function(o){
            var sf = this,
                bodys = sf.body.query('>.tab'),//Ext.DomQuery.select('div.tab',this.body.dom),
                strips = sf.head.query('div.strip'),//Ext.DomQuery.select('div.strip',this.head.dom),
                strip,body;
            if(Ext.isNumber(o)){
                if(o<0)o+=strips.length;
                o=Math.round(o);
                if(strips[o]){
                    strip=Ext.get(strips[o]);
                    body=Ext.get(bodys[o]);
                }
            }else {
                o=Ext.get(o);
                o=Ext.each(strips,function(s,i){
                    if(s == o.dom){
                        strip=o;
                        body=Ext.get(bodys[i]);
                        return false;
                    }
                });
            }
            if(strip){
                body.loaded = !(sf.items[o].ref && body.loaded!=true);
                return {'strip':strip,'body':body,'index':o};
            }else{
                return null;
            }
        },
        scrollTo : function(lr){
            var sf = this,
                stripwrap = sf.stripwrap,
                scrollRight = sf.scrollRight,
                scrollLeft = sf.scrollLeft,
                sl = stripwrap.getScroll().left,
                sw = sf.stripwidth;
            if(lr==LEFT){
                stripwrap.scrollTo(LEFT,sl-sw);
                scrollRight.removeClass(sd);
                if(stripwrap.getScroll().left<=0){
                    scrollLeft.addClass(sd).replaceClass(tslo,tsl);
                    sf.stopScroll();
                }
            }else if(lr==RIGHT){
                stripwrap.scrollTo(LEFT,sl+sw);
                scrollLeft.removeClass(sd);
                if(stripwrap.getScroll().left+stripwrap.getWidth()>=sf.head.getWidth()){
                    scrollRight.addClass(sd).replaceClass(tsro,tsr);
                    sf.stopScroll();
                }
            }
        },
        stopScroll : function(){
            if(this.scrollInterval){
                clearInterval(this.scrollInterval);
                delete this.scrollInterval;
            }
        },
        onClick : function(e,t){
            var el=Ext.fly(t),strip=el.parent($STRIP);
            /*
             * 单击鼠标，移除tab-btn-over
             */
            if(strip){
                if(strip.hasClass(tbo)){
                    strip.removeClass(tbo);
                }
            }
            if(el.hasClass(tc))this.closeTab(el.parent($STRIP));
        },
        onMouseWheel : function(e){
            var delta = e.getWheelDelta();
            if(delta > 0){
                this.scrollTo(LEFT);
                e.stopEvent();
            }else if (delta < 0){
                this.scrollTo(RIGHT);
                e.stopEvent();
            }
        },
        onMouseDown : function(e,t){
            var el=Ext.fly(t),strip = el.parent($STRIP),sf=this;
            if(el.hasClass(tc)){
                el.removeClass(tbo).addClass(tbd);
            }else if(el.hasClass(ts) && !el.hasClass(sd)){
                if(el.hasClass(tslo))sf.scrollTo(LEFT);
                else sf.scrollTo(RIGHT);
                sf.scrollInterval=setInterval(function(){
                    if(el.hasClass(ts)&&!el.hasClass(sd)){
                        if(el.hasClass(tslo))sf.scrollTo();
                        else sf.scrollTo(RIGHT);
                        if(el.hasClass(sd))clearInterval(sf.scrollInterval);
                    }
                },100);
            }else if(strip && strip.hasClass(STRIP) && !strip.hasClass(ACTIVE) && !strip.hasClass(sd)){
                sf.selectTab(strip);
            }
        },
        onMouseUp : function(e){
            this.stopScroll();
        },
        onMouseOver : function(e,t){
            var el=Ext.fly(t),strip = el.parent($STRIP);
            if(el.hasClass(ts)&&!el.hasClass(sd)){
                if(el.hasClass(tsl))el.replaceClass(tsl,tslo);
                else if(el.hasClass(tsr))el.replaceClass(tsr,tsro);
            } else if(el.hasClass(tc)){
                el.addClass(tbo);
            }
            if(strip){
                el = strip.child('div.'+tc);
                if(el){
                    var b = this.currentBtn;
                    if(b)b.hide();
                    this.currentBtn=el;
                    el.show();
                }
            }
            A.Tab.superclass.onMouseOver.call(this,e);
        },
        onMouseOut : function(e,t){
            var el=Ext.fly(t),strip = el.parent($STRIP);
            if(el.hasClass(ts)&&!el.hasClass(sd)){
                this.stopScroll();
                if(el.hasClass(tslo))el.replaceClass(tslo,tsl);
                else if((el.hasClass(tsro)))el.replaceClass(tsro,tsr);
            }else if(el.hasClass(tc)){
                el.removeClass([tbo,tbd]);
            }
            if(strip){
                el = strip.child('div.'+tc);
                if(el){
                    el.hide();
                }
            }
            A.Tab.superclass.onMouseOut.call(this,e);
        },
        showLoading : function(dom){
            A.Masker.mask(dom,_lang['tab.loading']);
//      dom.setStyle({'text-align':'center','line-height':5}).update(_lang['tab.loading']);
        },
        clearLoading : function(dom){
            A.Masker.unmask(dom);
//      dom.setStyle({'text-align':_N,'line-height':_N}).update(_N);
        },
        reloadTab : function(index,url){
            index = Ext.isEmpty(index) ? this.selectedIndex:index;
            var sf = this,tab=sf.getTab(index);
            if(!tab)return;
            if(url) sf.items[index].ref = url;
            sf.selectTab(index,true);
        },
        load : function(url,dom,index){
            var sf = this,body = Ext.get(dom);
            var firstTab = sf.wrap.child("div.tab-close");
            if(sf.selectedIndex == "0" && firstTab){
                firstTab.remove();
            }
            Ext.iterate(body.cmps,function(key,cmp){
                if(cmp.destroy){
                    try{
                        cmp.destroy();
                    }catch(e){
                        alert(ERROR + e)
                    }
                }
            });
            body.update(_N);
            body.cmps={};
            if(body.dom.tagName.toLowerCase() == 'iframe'){
                var url = Ext.urlAppend(url,Ext.urlEncode(Ext.apply({},{_sph:body.getHeight(),_spw:body.getWidth()})));
                body.dom.src = url;
            }else {
                if(url.indexOf("user_role_home_page")==-1){
                    sf.showLoading(body);
                }
                //TODO:错误信息
                Ext.Ajax.request({
                    //          url: Ext.urlAppend(url,'_vw='+sf.width+'&_vh='+(sf.height-sf.head.getHeight())),
                    url: url,
                    failure: function(response, opts){
                        sf.clearLoading(body);
                        var msg=['<div style="text-align:center;line-height:30px">'];
                        switch(response.status){
                            case 404:
                                msg.push('<H2>',response.status , _lang['ajax.error'],'</H2>', _lang['ajax.error.404']+'"'+ response.statusText+'"');
                                break;
                            case 500:
                                msg.push('<H2>',response.status , _lang['ajax.error'],'</H2>',_lang['tab.internet.error'],'<a href="javascript:$le(\''+sf.id+'\').selectTab('+index+')">',_lang['tab.internet.refresh'],'</a>');
                                break;
                            case 0:
                                break;
                            default:
                                msg.push(_lang['ajax.error'], response.statusText);
                                break;
                        }
                        msg.push('</div>');
                        body.update(msg.join(''));
                        body.loading = false;
                    },
                    success: function(response, options){
                        var res;
                        try {
                            res = Ext.decode(response.responseText);
                        }catch(e){}
                        if(res && res.success == false){
                            if(res.error){
                                if(res.error.code  && res.error.code == 'session_expired' || res.error.code == 'login_required'){
                                    if(A.manager.fireEvent('timeout', A.manager))
                                        return;
                                }else{
                                    A.manager.fireEvent('ajaxfailed', A.manager, options.url,options.para,res);
                                    var st = res.error.stackTrace,
                                        em = res.error.message;
                                    st = st ? st.replaceAll('\r\n','</br>') : _N;
                                    A.showErrorMessage(_lang['window.error'], em?em+'</br>'+st:st,null,400,em && st==_N ? 150 : 250);
                                }
                            }
                            body.loading = false;
                            return;
                        }
                        var html = response.responseText;
                        //              sf.intervalIds[index]=setInterval(function(){
                        //                  if(!A.focusTab){
                        //                      clearInterval(sf.intervalIds[index]);
                        //                      A.focusTab=body;
                        //                      try{
                        body.set({url:url});
                        body.update(html,true,function(){
                            sf.clearLoading(body);
                            //                              A.focusTab=null;
                            body.loaded = true;
                            body.loading = false;
                            sf.fireEvent(EVT_SELECT, sf, index)
                        },body);
                        //                      }catch(e){
                        //                          A.focusTab=null;
                        //                      }
                        //                  }
                        //              },10)
                    }
                });
            }
        },
        setWidth : function(w){
            w = Math.max(w,2);
            var sf = this;
            if(sf.width==w)return;
            A.Tab.superclass.setWidth.call(sf, w);
            var body = sf.body,head = sf.head,stripwrap = sf.stripwrap,
                scrollLeft = sf.scrollLeft,scrollRight= sf.scrollRight;
            stripwrap.setWidth(w-38);
            if(w-38<head.getWidth()){
                scrollLeft.setStyle({display:_N});
                scrollRight.setStyle({display:_N});
                stripwrap.setStyle(PADDING_LEFT,'1px');
                var sl=stripwrap.getScroll().left,sw=stripwrap.getWidth(),hw=head.getWidth();
                if(sl<=0)scrollLeft.addClass(sd);
                else scrollLeft.removeClass(sd);
                if(sl+sw>=hw){
                    if(!scrollRight.hasClass(sd))scrollRight.addClass(sd);
                    else stripwrap.scrollTo(LEFT,hw-sw);
                }else scrollRight.removeClass(sd);
            }else{
                scrollLeft.setStyle({display:NONE});
                scrollRight.setStyle({display:NONE});
                stripwrap.setStyle(PADDING_LEFT,'0').scrollTo(LEFT,0);
            }
        },
        setHeight : function(h){
            h = Math.max(h,25);
            if(this.height==h)return;
            A.Tab.superclass.setHeight.call(this, h);
            this.body.setHeight(h-26);
            if(window['tabSetHeightCall']){
                tabSetHeightCall();
            }
        },
        setMainHeight:function(h){
            h = Math.max(h,25);
            if(this.height==h)return;
            A.Tab.superclass.setHeight.call(this, h);
            this.body.setHeight(h);
        }
    });
})($L);