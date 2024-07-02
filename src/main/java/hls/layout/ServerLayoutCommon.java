package hls.layout;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.utils.SpringContextHolder;
import hls.layout.service.IServerLayoutService;
import leaf.database.service.IDatabaseServiceFactory;
import leaf.database.service.ServiceOption;
import leaf.plugin.script.engine.ScriptRunner;
import leaf.service.ServiceContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uncertain.cache.ICache;
import uncertain.cache.INamedCacheFactory;
import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.JSONAdaptor;
import uncertain.ocm.IObjectRegistry;

import java.util.*;

public class ServerLayoutCommon {
    String layout_code = null;
    String function_code = null;
    String bp_seq = "";
    String layout_service_name = null;
    String p_;
    String ns_;
    CompositeMap params = null;
    ServiceContext svcContext;
    CompositeMap context = null;
    CompositeMap config;
    CompositeMap enableTabResultMap = null;
    CompositeMap viewScript = null;
    List<CompositeMap> tabResult = null;
    List<CompositeMap> buttonResult = null;
    IDatabaseServiceFactory dsf;
    IObjectRegistry or;
    private ICache cache;
    private IServerLayoutService serverLayoutService;
    private boolean isTreeTab = false;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String RIGHT_NAV_BAR = "RIGHT_NAV_BAR";
    private static final String HIDE_BUTTON_CODE = "hidden_button_code";

    public ServerLayoutCommon(IDatabaseServiceFactory dsf, IObjectRegistry or,
                              ServiceContext context, CompositeMap config) {
        this.dsf = dsf;
        this.or = or;
        this.svcContext = context;
        this.config = config;
        this.context = svcContext.getObjectContext();
        this.params = svcContext.getParameter();
        INamedCacheFactory cacheProvider = (INamedCacheFactory) or
                .getInstanceOfType(INamedCacheFactory.class);
        cache = cacheProvider.getNamedCache("DataCache");
        serverLayoutService = SpringContextHolder.getBean(IServerLayoutService.class);
    }

    public CompositeMap getParams() {
        return this.params;
    }


    private void CreateEvent(CompositeMap events, String name, String handle) {
        CompositeMap event = events.createChild(events.getPrefix(),
                events.getNamespaceURI(), "event");
        event.put("name", name);
        event.put("handler", handle);
    }

    private void createFieldMapping(CompositeMap mapping, String mapFrom,
                                    String mapTo) {
        CompositeMap map = mapping.createChild(mapping.getPrefix(),
                mapping.getNamespaceURI(), "map");
        map.put("to", mapTo);
        map.put("from", mapFrom);
    }

    private void createRightNavBar(CompositeMap rightNavBars, String id,
                                   String click, String text, String icon) {
        CompositeMap rightNavBar = rightNavBars.createChild(
                rightNavBars.getPrefix(),
                rightNavBars.getNamespaceURI(), "rightNavBar");
        rightNavBar.put("id",id);
        rightNavBar.put("click", click);
        rightNavBar.put("defaultimage", icon);
        rightNavBar.put("title", text);
    }

    private void createFunctionButton(CompositeMap screenTopToolbar, String id,
                                      String click, String text) {
        CompositeMap functionButton = screenTopToolbar.createChild(
                screenTopToolbar.getPrefix(),
                screenTopToolbar.getNamespaceURI(), "gridButton");
        functionButton.put("id", layout_code + "_" + id);
        functionButton.put("click", click);
        functionButton.put("text", text);
    }

    public void run() throws Exception {
		/*if (!layoutAccessCheck()) {
			layoutCheckError();
			return;
		}*/
        Map<String, Object> mainQueryParameters = new HashMap<>();
        layout_code = params.getString("layout_code");
        function_code = params.getString("function_code");
        if (layout_code == null) {
            throw new Exception("layout_code is null");
        }

        if (params.getString("tree_code") != null) {
//			每次渲染单独初始化此对象，所以使用成员变量没有线程安全问题
            isTreeTab = true;
            /*判断逻辑调整到查询部分进行*/
            // tabConfigImport = treeTabConfigImport;
        }
        mainQueryParameters.put("layout_code", layout_code);
        mainQueryParameters.put("tree_code", params.getString("tree_code"));
        Map<String, Object> allTabParametes = new HashMap<>();
        allTabParametes.put("layout_code", layout_code);
        allTabParametes.put("enabled_flag", "Y");

        this.enableTabResultMap = selectLayoutTabOrTree(allTabParametes);
//		queryData(tabConfigImport, allTabParametes);

        CompositeMap resultMap = serverLayoutService.selectLayoutMain(mainQueryParameters);
//		queryData(layoutConfigImport,mainQueryParameters);

//        FIXME: test saveBtn
//        mainQueryParameters.put("function_code", "HLS217");
//        end
        mainQueryParameters.put("function_code", function_code);
        CompositeMap buttonResultMap = serverLayoutService.selectLayoutButton(mainQueryParameters);
//        CompositeMap buttonResultMap = queryData(buttonConfigImport, mainQueryParameters);
        if (buttonResultMap != null) {
            this.buttonResult = buttonResultMap.getChilds();
        }


        if (resultMap != null) {
            List<CompositeMap> result = resultMap.getChilds();
            if (result != null)
                createList(result, context);
        }
        if ("Y".equals(params.getString("layout_debugger_flag"))) {
            System.out.println(config.toXML());
        }
    }

    private void layoutCheckError() {
        CompositeMap view = CompositeUtil.findChild(config, "view");
        if (view == null) {
            view = config.createChild(config.getPrefix(),
                    config.getNamespaceURI(), "view");
        }
        this.p_ = view.getPrefix();
        this.ns_ = view.getNamespaceURI();
        if (view.getChild("script") == null) {
            CompositeMap script = view.createChild("script");
        }
        this.viewScript = view.getChild("script");
        String checkMessage = "'please check layout_config.js of '+" + "'"
                + this.layout_service_name + "'";
        this.viewScript.setText("alert(" + checkMessage + ");");
    }

    private boolean layoutAccessCheck() {
        try {
            String serviceName = context.getString("service_name");
            ScriptRunner sr = new ScriptRunner(str("enableLayoutConfig['", serviceName, "']"), context, or);
            this.layout_service_name = serviceName;
            sr.setImport("layoutconfig/layout_config.js");
            Object result = sr.run();
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
        } catch (Exception e) {
            logger.error("layout access check error.", e);
        }

        return false;
    }

    private void createList(List<CompositeMap> result, CompositeMap context)
            throws Exception {
        CompositeMap view = CompositeUtil.findChild(config, "view");
        if (view == null) {
            view = config.createChild(config.getPrefix(),
                    config.getNamespaceURI(), "view");
        }
        this.p_ = view.getPrefix();
        this.ns_ = view.getNamespaceURI();

        List<CompositeMap> viewList = view.getChilds();
        if (viewList != null) {
            for (CompositeMap child : viewList) {
                if ("script".equals(child.getName()) && child.getString("src") == null) {
                    this.viewScript = child;
                    break;
                }
            }
        }
        if (this.viewScript == null) {
            this.viewScript = view.createChild("script");
        }

        CompositeMap style = null;
        if (view.getChild("style") == null) {
            style = new CompositeMap("style");
            view.addChild(view.getChilds().indexOf(this.viewScript), style);
        } else {
            style = view.getChild("style");
        }
        String layout_gridbox_button_hide = "\n.layout_gridbox_button_hide .gridbox-close-button,.layout_gridbox_button_hide .gridbox-button{display:none}\n";
        if (style.getText() == null) {
            style.setText(layout_gridbox_button_hide);
        } else {
            style.setText(style.getText() + layout_gridbox_button_hide);
        }
        String layoutDataSetList = str("if(typeof(", layout_code, "_layoutDataSetList" + ") == 'undefined'){var ", layout_code, "_layoutDataSetList=[];\n}\n");
        String rootDataSetList = str("if(typeof(", layout_code, "_rootDataSetList" + ") == 'undefined'){var ", layout_code, "_rootDataSetList=[];\n}\n");
        String layoutDataSetObj = str("if(typeof(", layout_code, "_layoutDataSetObj", ") == 'undefined'){var ", layout_code, "_layoutDataSetObj=[];\n}\n");
        if (this.viewScript.getText() == null) {
            this.viewScript.setText(layoutDataSetList + rootDataSetList
                    + layoutDataSetObj);

        } else {
            this.viewScript.setText(this.viewScript.getText()
                    + layoutDataSetList + rootDataSetList + layoutDataSetObj);
        }
        this.viewScript.setText(this.viewScript.getText()
                + ServerLayoutScript.getParserScript(context, "COMMON",
                this.params));
        String jsonString = JSONAdaptor.toJSONObject(this.params).toString();
        String all_parameters = "window['" + layout_code
                + params.getString("tree_code", "") + "_all_parameters']";
        this.viewScript.setText(all_parameters + "=" + jsonString + ";\n"
                + this.viewScript.getText());

        CompositeMap screenInclude = new CompositeMap(p_, ns_, "screen-include");
        String loadScreen = str("modules/layout/flexible_UI.lview?layout_code=", layout_code, "&winid=${/parameter/@winid}");
        screenInclude.put("screen", loadScreen);
        int index = view.getChilds().indexOf(this.viewScript);
        view.getChilds().add(index, screenInclude);
        CompositeMap dataSets = CompositeUtil.findChild(config, "dataSets");
        if (dataSets == null) {
            dataSets = view.createChild(p_, ns_, "dataSets");
        }

        CompositeMap allTabDataSet = dataSets.createChild(p_, ns_, "dataSet");
        allTabDataSet.put("id", str(layout_code, params.getString("tree_code", ""), "_all_tab_config_ds"));
        CompositeMap vritualDataSet = CompositeUtil.findChild(config, "dataSet", "id", layout_code + "_virtual_ds");
        if (vritualDataSet == null && "0".equals(params.getString("tree_index", "0"))) {
            vritualDataSet = dataSets.createChild(p_, ns_, "dataSet");
            vritualDataSet.put("id", layout_code + "_virtual_ds");
            vritualDataSet
                    .put("submiturl",
                            str(
//                                    "${/request/@context_path}/modules/layout/server_layout_common_save.lsc?function_code="
                                    "${/request/@context_path}/server/layout/save?function_code="
                                    , params.getString("function_code", "")
                                    , "&function_usage="
                                    , params.getString("function_usage", "")
                                    , "&number_of_tenant="
                                    , params.getString("number_of_tenant", "")
                                    , "&number_of_guarantor="
                                    , params.getString("number_of_guarantor", "")
                                    , "&number_of_actual_controller="
                                    , params.getString("number_of_actual_controller", "")
                                    , "&number_of_quotation="
                                    , params.getString("number_of_quotation", "")
                                    , "&layout_code="
                                    , layout_code
                                    , "&tree_code="
                                    , params.getString("tree_code", "")
                                    , "&dynamic_source_table="
                                    , params.getString("dynamic_source_table", "")
                                    , "&dynamic_source_table_pk_value="
                                    , params.getString("dynamic_source_table_pk_value", "")
                            )
                    );
            vritualDataSet.put("autocreate", "true");
        }
        CompositeMap datas = allTabDataSet.createChild(
                allTabDataSet.getPrefix(), allTabDataSet.getNamespaceURI(),
                "datas");
        this.enableTabResultMap.setName("allTabResult");
        this.svcContext.getModel().addChild(this.enableTabResultMap);
        List<CompositeMap> allTabResult = this.enableTabResultMap.getChilds();
        if (allTabResult != null)
            for (CompositeMap tabChild : allTabResult) {
                CompositeMap datasRecord = datas.createChild(datas.getPrefix(),
                        datas.getNamespaceURI(), "record");
                datasRecord.putAll(tabChild);
            }

        CompositeMap screenBody = view.createChild(p_, ns_, "screenBody");
        screenBody.put("padding", 0);
//		screenBody.put("style", "width:98%;");

        if (params.getString("tree_code") == null) {
            List<CompositeMap> tMaps = new ArrayList<>();
            List<CompositeMap> bMaps = new ArrayList<>();
            List<CompositeMap> rMaps = new ArrayList<>();
            if (this.buttonResult != null) {
                for (int i = 0; i < this.buttonResult.size(); i++) {
                    CompositeMap map = this.buttonResult.get(i);
                    if (map.get("button_type") != null) {
                        if ("right".equalsIgnoreCase(map.get("button_type").toString())) {
                            rMaps.add(this.buttonResult.get(i));
                        } else if ("bottom".equalsIgnoreCase(map.get("button_type").toString())) {
                            bMaps.add(this.buttonResult.get(i));
                        } else {
                            tMaps.add(this.buttonResult.get(i));
                        }
                    } else {
                        tMaps.add(this.buttonResult.get(i));
                    }

                }
            }
            if (rMaps.size() > 0) {
                createRightNavBars(rMaps, screenBody, view);
            }
            if (bMaps.size() > 0) {
                createScreenTopToolbar(bMaps, screenBody, view, "bottom");
            }
            if (tMaps.size() > 0) {
                createScreenTopToolbar(tMaps, screenBody, view, "top");
            }
        }
        CompositeMap parentTabConfig = null;
        createMain(result, screenBody, dataSets, parentTabConfig);
    }

    private void createMain(List<CompositeMap> result, CompositeMap parent,
                            CompositeMap dataSets, CompositeMap parentTabConfig)
            throws Exception {
        for (CompositeMap child : result) {
            if ("TAB".equals(child.getString("tab_type"))) {
                createTabPanel(child, parent, dataSets, parentTabConfig);
            } else if ("LEFT_NAV_BAR".equals(child.getString("tab_type"))) {
                createLeftNavBars(child, parent, parentTabConfig, dataSets, result);
            } else if (RIGHT_NAV_BAR.equals(child.getString("tab_type"))) {
                createLeftNavBars(child, parent, parentTabConfig, dataSets, result);
            } else {
                Map<String, Object> parametersNew = new HashMap<String, Object>();
                parametersNew.put("layout_code", layout_code);
                parametersNew.put("function_code", function_code);
                parametersNew.put("tab_code", child.getString("tab_code"));

                CompositeMap tabResultMap = null;
//				CompositeMap tabResultMap = queryData(tabConfigImport, parametersNew);
                tabResultMap = selectLayoutTabOrTree(parametersNew);

                List<CompositeMap> tabResult = tabResultMap.getChilds();
                CompositeMap screenBox = null;
                if (in(child.getString("tab_type"), "FIELDBOXCOLUMN", "QUERYFORM", "FORMTOOLBAR", "FORMBODY")
                        || (parentTabConfig != null && "ACCORDIONPANEL"
                        .equals(parentTabConfig.getString("tab_type")))) {
                    screenBox = parent;
                } else {
                    String screenBoxStyle;
                    if (parentTabConfig != null && "SWITCHCARD".equals(parentTabConfig.getString("tab_type"))) {
                        screenBox = parent;
                        screenBoxStyle = "width:98%;padding:0px 0px 0px 0px";
                    } else {
                        screenBox = parent.createChild(parent.getPrefix(), parent.getNamespaceURI(), "box");
                        if ("TAB".equalsIgnoreCase(parent.getQName().getLocalName())) {
                            screenBoxStyle = "width:98%;padding:0px 0px 0px 0px";
                        } else {
                            screenBoxStyle = "width:98%;padding:0px 0px 0px 0px";
                        }
                    }
                    screenBox.put("style", screenBoxStyle);
                }
                if (tabResult != null) {
                    for (CompositeMap tab_child : tabResult) {
                        createComponent(tab_child, screenBox, dataSets);
                    }
                }
            }
        }
    }

    private CompositeMap selectLayoutTabOrTree(Map<String, Object> parametersNew) {
        CompositeMap tabResultMap;
        if (!isTreeTab) {
            tabResultMap = serverLayoutService.selectLayoutTab(parametersNew);
        } else {
            tabResultMap = serverLayoutService.selectLayoutTreeTab(parametersNew);
        }
        return tabResultMap;
    }

    private void createRightNavBars(List<CompositeMap> buttonResult,
                                    CompositeMap parent, CompositeMap view) {
        CompositeMap rightNavBars1 = CompositeUtil.findChild(config, "rightNavBars");
        if (rightNavBars1 != null || this.buttonResult != null) {
            CompositeMap rightNavBars = parent.createChild(p_, ns_, "rightNavBars");
            if (rightNavBars1 != null) {
                List<CompositeMap> rightNavBars1Child = rightNavBars1.getChilds();
                for (CompositeMap defaultButtonChild : rightNavBars1Child) {
                    rightNavBars.addChild(defaultButtonChild);
                }
                view.removeChild(rightNavBars1);
            }
            rightNavBars.put("id", layout_code + "_right_nav_bars");
            rightNavBars.put("right", "30");
            if (buttonResult != null&&!buttonResult.isEmpty()) {
                for (CompositeMap buttonChild : buttonResult) {
                    String button_code = buttonChild.getString("button_code");
                    String buttonScript = ServerLayoutScript.getParserScript(
                            context, button_code + "BUTTON", this.params);
                    if (buttonScript != null) {
                        createRightNavBar(rightNavBars,
                                button_code.toLowerCase(),
                                str(bp_seq, layout_code, "_", button_code, "_LAYOUT_DYNAMIC_CLICK"),
                                buttonChild.getString("prompt"),
                                buttonChild.getString("icon"));
                        this.viewScript.setText(buttonScript + this.viewScript.getText());
                    } else {
                        if (this.viewScript.getText().indexOf(str("${/parameter/@layout_code}_", button_code.toLowerCase(), "_layout_dynamic_click")) != -1) {
                            createRightNavBar(
                                    rightNavBars,
                                    button_code.toLowerCase(),
                                    str(layout_code, "_", button_code.toLowerCase(), "_layout_dynamic_click"),
                                    buttonChild.getString("prompt"),
                                    buttonChild.getString("icon"));
                        }
                    }
                }
                createRightNavBar(
                        rightNavBars,
                        "display_common_button_id",
                        "display_common_button_click",
                        " ",
                        "resources/images/hide-common.png");
            }
        }

    }

    private void createScreenTopToolbar(List<CompositeMap> buttonResult,
                                        CompositeMap parent, CompositeMap view, String type) {

        if (this.buttonResult != null) {
            CompositeMap screenTopToolbar = parent.createChild(p_, ns_,
                    "screenTopToolbar");
            screenTopToolbar.put("type", type);
            screenTopToolbar.put("style", "height:42px;padding-left:10px;");
            CompositeMap screenTitle = CompositeUtil.findChild(screenTopToolbar, "screenTitle");
            if (screenTitle == null) {
                screenTitle = screenTopToolbar.createChild(p_, ns_, "screenTitle");
            }
            if (buttonResult != null) {
                for (CompositeMap buttonChild : buttonResult) {
                    String button_code = buttonChild.getString("button_code");
                    String query_display_flag = buttonChild.getString("query_display_flag", "N");
                    if (checkButtonHide(button_code) || ("QUERY".equalsIgnoreCase(this.params.getString("function_usage")) && "N".equalsIgnoreCase(query_display_flag))) {
                        continue;
                    }
                    String buttonScript = ServerLayoutScript.getParserScript(
                            context, button_code + "BUTTON", this.params);
                    if (buttonScript != null) {
                        createFunctionButton(screenTopToolbar,
                                button_code.toLowerCase(), bp_seq + layout_code
                                        + "_" + button_code
                                        + "_LAYOUT_DYNAMIC_CLICK",
                                buttonChild.getString("prompt"));
                        this.viewScript.setText(buttonScript
                                + this.viewScript.getText());
                    } else {
                        if (this.viewScript.getText().indexOf(
                                str("${/parameter/@layout_code}_"
                                        , button_code.toLowerCase()
                                        , "_layout_dynamic_click")) != -1) {
                            createFunctionButton(
                                    screenTopToolbar,
                                    button_code.toLowerCase(),
                                    str(layout_code + "_", button_code.toLowerCase(), "_layout_dynamic_click"),
                                    buttonChild.getString("prompt"));
                        }
                    }
                }
            }
        }
    }

    public boolean checkButtonHide(String buttonCode) {
        if (params.get("hidden_button_code") != null) {
            /*if (params.get("hidden_button_code") instanceof String) {
                return buttonCode.equalsIgnoreCase(params.getString("hidden_button_code"));
            }*/
            if (params.get("hidden_button_code") instanceof String) {
                //转成数组,判断是否要隐藏按钮
                String[] igButtons = ((String) params.get("hidden_button_code")).split(",");
                return Arrays.asList(igButtons).contains(buttonCode);
            }
            String[] hideCodeArr = (String[]) params.get("hidden_button_code");
            for (int i = 0; i < hideCodeArr.length; i++) {
                if (buttonCode.equalsIgnoreCase(hideCodeArr[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    private void createFieldBox(CompositeMap child, CompositeMap parent,
                                CompositeMap dataSets) throws Exception {
        CompositeMap fieldBox = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "fieldBox");
        fieldBox.put("fieldheight", child.get("field_height"));
        fieldBox.put("fieldwidth", child.get("field_width"));
        fieldBox.put("title", child.getString("tab_desc"));
        fieldBox.put("colspan", child.get("colspan"));
        fieldBox.put("rowspan", child.getString("rowspan"));
        fieldBox.put("labelseparator", " ");
        CompositeMap parametersNew = new CompositeMap();
        parametersNew.put("layout_code", layout_code);
        parametersNew.put("function_code", function_code);
        parametersNew.put("parent_tab_code", child.get("tab_code"));
        parametersNew.put("enabled_flag", "Y");

        CompositeMap fieldBoxResultMap = selectLayoutTabOrTree(parametersNew);
//		queryData(tabConfigImport, parametersNew);

        if (fieldBoxResultMap != null) {
            List<CompositeMap> result = fieldBoxResultMap.getChilds();
            if (result != null) {
                CompositeMap fieldBoxColumns = fieldBox.createChild(
                        parent.getPrefix(),
                        parent.getNamespaceURI(),
                        "fieldBoxColumns");
                createMain(result, fieldBoxColumns, dataSets, child);
            }
        }

    }

    private Integer getStringNumber(String name) {
        if (StringUtils.isEmpty(params.getString(name))) {
            return 0;
        } else {
            return params.getInt(name, 0);
        }
    }

    private void createLeftNavBars(CompositeMap child, CompositeMap parent,
                                   CompositeMap parentBarConfig, CompositeMap dataSets, List<CompositeMap> result) throws Exception {
        CompositeMap finalComponent = null;
        if (parentBarConfig != null) {

        } else {
            finalComponent = parent;
        }
        CompositeMap leftNavBars = CompositeUtil.findChild(config, "leftNavBars");
        if (leftNavBars == null) {
            leftNavBars = finalComponent.createChild(parent.getPrefix(), parent.getNamespaceURI(), "leftNavBars");
        }
        if (leftNavBars.get("type") == null) {
            for (int i = 0; i < result.size(); i++) {
                for (int j = i + 1; j < result.size(); j++) {
                    if (result.get(i).get("tab_group").toString().equals(result.get(j).get("tab_group").toString())) {
                        leftNavBars.put("type", "tree");
                    }
                }
            }
            leftNavBars.putIfAbsent("type", "normal");
            /*if (leftNavBars.get("type") == null) {
                leftNavBars.put("type", "normal");
            }*/
        }
//		ServiceOption so = ServiceOption.createInstance();
//		so.setDefaultWhereClause("((t1.tab_group=${@tab_group} and nvl(t1.parent_tab_code,'-999')=nvl(${@parent_tab_code},'-999') and nvl(${@tab_tab_code},'-999')='-999') or (nvl(${@tab_code},'-999')=t1.tab_code)) and t1.tab_type='LEFT_NAV_BAR' and t1.enabled_flag='Y'");
        CompositeMap parametersNew = new CompositeMap();
        parametersNew.put("layout_code", layout_code);
        parametersNew.put("function_code", function_code);
        parametersNew.put("tab_group", child.get("tab_group"));
        parametersNew.put("tab_code", child.get("tab_code"));
        parametersNew.put("leaf_default_where_sql", "((t1.tab_group=#{tab_group} and nvl(t1.parent_tab_code,'-999')=nvl(#{parent_tab_code},'-999') and nvl(#{tab_tab_code},'-999')='-999') or (nvl(#{tab_code},'-999')=t1.tab_code)) and t1.tab_type='LEFT_NAV_BAR' and t1.enabled_flag='Y'");

        CompositeMap tabResultMap = selectLayoutTabOrTree(parametersNew);
//		CompositeMap tabResultMap = queryData(tabConfigImport, parametersNew, so);

        if (tabResultMap != null) {
            this.tabResult = tabResultMap.getChilds();
            if (tabResult != null) {
                for (CompositeMap bar : tabResult) {
                    createLeftNavBar(bar, leftNavBars, dataSets, result);
                }
            }
        }
    }

    private void createTabPanel(CompositeMap child, CompositeMap parent,
                                CompositeMap dataSets, CompositeMap parentTabConfig)
            throws Exception {
        CompositeMap finalComponent = null;
        if (parentTabConfig != null) {
            finalComponent = parent.createChild(parent.getPrefix(),
                    parent.getNamespaceURI(), "box");
            finalComponent.put("padding", 0);
            finalComponent.put("style", "width:98%;padding:0px 0px 0px 5px");
        } else {
            finalComponent = parent;
        }
        CompositeMap tabPanel = finalComponent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "tabPanel");
        CompositeMap tabs = tabPanel.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "tabs");
//		ServiceOption so = ServiceOption.createInstance();
//		so.setDefaultWhereClause("((t1.tab_group=${@tab_group} and nvl(t1.parent_tab_code,'-999')=nvl(${@parent_tab_code},'-999') and nvl(${@tab_tab_code},'-999')='-999') or (nvl(${@tab_tab_code},'-999')=t1.tab_code)) and t1.tab_type='TAB' and t1.enabled_flag='Y'");
        CompositeMap parametersNew = new CompositeMap();
        parametersNew.put("layout_code", layout_code);
        parametersNew.put("function_code", function_code);
        parametersNew.put("tab_group", child.get("tab_group"));
        if (parentTabConfig != null) {
            parametersNew.put("parent_tab_code",
                    parentTabConfig.getString("tab_code"));
        }
        parametersNew.put("leaf_default_where_sql", "((t1.tab_group=#{tab_group} and nvl(t1.parent_tab_code,'-999')=nvl(#{parent_tab_code},'-999') and nvl(#{tab_tab_code},'-999')='-999') or (nvl(#{tab_tab_code},'-999')=t1.tab_code)) and t1.tab_type='TAB' and t1.enabled_flag='Y'");
        CompositeMap tabResultMap = selectLayoutTabOrTree(parametersNew);
//		CompositeMap tabResultMap = queryData(tabConfigImport, parametersNew, so);
        if (tabResultMap != null) {
            this.tabResult = tabResultMap.getChilds();
            String firstTabFlag = "Y";
            if (tabResult != null) {
                for (CompositeMap tab : tabResult) {
                    if ("Y".equals(firstTabFlag)) {
                        tabPanel.put("height", tab.get("height"));
                        tabPanel.put("marginheight", tab.get("margin_height"));
                        tabPanel.put("id", layout_code + "_TAB_GROUP_" + tab.get("tab_group")); //tabPanel加id,便于前台获取tabPanel,tab_group:分组号
                        tabPanel.put("marginwidth", tab.get("margin_width"));
                        tabPanel.put("width", tab.get("width"));
                        if (parentTabConfig != null) {
                            tabPanel.put("style", "padding:0px 0px 0px 0px");
                        } else {
                            tabPanel.put("style", "padding:0px 0px 0px 0px");
                        }
                    }
                    firstTabFlag = "N";
                    String repeat_object = tab.getString("repeat_object", "");
                    if (("TENANT".equals(repeat_object) && getStringNumber("number_of_tenant") >= 1)
                            || ("GUARANTOR".equals(repeat_object) && getStringNumber("number_of_guarantor") >= 1)
                            || ("ACTUAL_CONTROLLER".equals(repeat_object) && getStringNumber("number_of_actual_controller") >= 1)
                            || ("QUOTATION".equals(repeat_object) && getStringNumber("number_of_quotation") >= 1)) {
                        int repeatNumber = 0;
                        if ("TENANT".equals(repeat_object)) {
                            repeatNumber = getStringNumber("number_of_tenant");
                        } else if ("GUARANTOR".equals(repeat_object)) {
                            repeatNumber = getStringNumber("number_of_guarantor");
                        } else if ("ACTUAL_CONTROLLER".equals(repeat_object)) {
                            repeatNumber = getStringNumber("number_of_actual_controller");
                        } else if ("QUOTATION".equals(repeat_object)) {
                            repeatNumber = getStringNumber("number_of_quotation");
                        }
                        for (int i = 1; i <= repeatNumber; i++) {
                            params.put("bp_seq", i);
                            bp_seq = params.getString("bp_seq", "");
                            String layoutDataSetList = "window['" + bp_seq
                                    + layout_code
                                    + "_layoutDataSetList']=[];\n";
                            String rootDataSetList = "window['" + bp_seq
                                    + layout_code + "_rootDataSetList']=[];\n";
                            String layoutDataSetObj = "window['" + bp_seq
                                    + layout_code + "_layoutDataSetObj']={};\n";
                            this.viewScript.setText(this.viewScript.getText()
                                    + layoutDataSetList + rootDataSetList
                                    + layoutDataSetObj);
                            this.viewScript.setText(this.viewScript.getText()
                                    + ServerLayoutScript.getParserScript(
                                    context, "COMMON", this.params));
                            CompositeMap view = CompositeUtil.findChild(config,
                                    "view");
                            CompositeMap screenInclude = new CompositeMap(p_,
                                    ns_, "screen-include");
                            String loadScreen = "modules/layout/flexible_UI.lview?layout_code="
                                    + layout_code
                                    + "&winid=${/parameter/@winid}&bp_seq="
                                    + bp_seq;
                            screenInclude.put("screen", loadScreen);
                            int index = view.getChilds().indexOf(
                                    this.viewScript);
                            view.getChilds().add(index, screenInclude);
                            if (repeatNumber == 1) {
                                createTab(tab, tabs, dataSets, "");
                            } else {
                                createTab(tab, tabs, dataSets, bp_seq);
                            }
                        }
                    } else if (("TENANT".equals(repeat_object) && getStringNumber("number_of_tenant") == 0)
                            || ("GUARANTOR".equals(repeat_object) && getStringNumber("number_of_guarantor") == 0)
                            || ("ACTUAL_CONTROLLER".equals(repeat_object) && getStringNumber("number_of_actual_controller") == 0)
                            || ("QUOTATION".equals(repeat_object) && getStringNumber("number_of_quotation") == 0)) {
                        this.viewScript
                                .setText(this.viewScript.getText()
                                        + "parent.$L.Masker.unmask(parent.Ext.get('mainFrame2_id'));\n $L.Masker.unmask(Ext.get(document.documentElement));\n");
                    } else {
                        createTab(tab, tabs, dataSets, "");
                    }
                }
            }
        }
    }

    private void createLeftNavBar(CompositeMap child, CompositeMap leftNavBars, CompositeMap dataSets, List<CompositeMap> list) throws Exception {
        CompositeMap bar = leftNavBars.createChild(leftNavBars.getPrefix(),
                leftNavBars.getNamespaceURI(), "leftNavBar");
        leftNavBars.putString("tab_code",child.getString("tab_code"));
        bar.put("title", child.getString("tab_desc"));
        bar.put("id", layout_code + "_" + child.getString("tab_code") + "_component_id");
        if ("tree".equals(leftNavBars.get("type"))) {
            int seq = child.getInt("tab_seq");
            int group = child.getInt("tab_group");
            int minSeq = seq;
            CompositeMap minMap = null;
            for (int i = 0; i < list.size(); i++) {
                if (group == Integer.parseInt(list.get(i).get("tab_group").toString())) {
                    if (minSeq > Integer.parseInt(list.get(i).get("tab_seq").toString())) {
                        minSeq = Integer.parseInt(list.get(i).get("tab_seq").toString());
                        minMap = list.get(i);
                    }
                }
            }
            if (minSeq == seq) {
                bar.put("grouptitle", child.getString("tab_desc"));
                bar.put("parentid", layout_code + "_" + child.getString("tab_code") + "_component_id");
            } else {
                bar.put("parentid", layout_code + "_" + (minMap.get("tab_code").toString()) + "_component_id");
            }
        }
        if (child.getString("tab_ref_screen") != null) {
            bar.put("ref",child.getString("tab_ref_screen"));
            return;
        }
        Map<String, Object> tabQueryParameters = new HashMap<>();
        tabQueryParameters.put("tab_tab_code", child.getString("tab_code"));
        tabQueryParameters.put("layout_code", layout_code);
        tabQueryParameters.put("function_code", function_code);
        tabQueryParameters.put("maintain_type", params.getString("maintain_type"));
        tabQueryParameters.put("calc_type", params.getString("calc_type"));
        CompositeMap resultMap = serverLayoutService.selectLayoutMain(tabQueryParameters);
//		queryData(layoutConfigImport,tabQueryParameters);
        if (resultMap != null) {
            List<CompositeMap> result = resultMap.getChilds();
            if (result != null) {
                createMain(result, bar, dataSets, leftNavBars);
            }
        }
    }

    private void createTab(CompositeMap child, CompositeMap tabs,
                           CompositeMap dataSets, String bp_seq) throws Exception {
        CompositeMap tab = tabs.createChild(tabs.getPrefix(),
                tabs.getNamespaceURI(), "tab");
        tab.put("prompt", child.getString("tab_desc") + bp_seq);
        tab.put("width", child.getInt("label_width", 100));
        if (child.getString("tab_ref_screen") != null) {
            tab.put("ref",
                    "${/request/@context_path}/"
                            + child.getString("tab_ref_screen"));
            return;
        }
        Map<String, Object> tabQueryParameters = new HashMap<>();
        tabQueryParameters.put("tab_tab_code", child.getString("tab_code"));
        tabQueryParameters.put("layout_code", layout_code);
        tabQueryParameters.put("function_code", function_code);
        tabQueryParameters.put("maintain_type",
                params.getString("maintain_type"));
        tabQueryParameters.put("calc_type", params.getString("calc_type"));
        CompositeMap resultMap = serverLayoutService.selectLayoutMain(tabQueryParameters);
//		queryData(layoutConfigImport,tabQueryParameters);
        if (resultMap != null) {
            List<CompositeMap> result = resultMap.getChilds();
            if (result != null) {
                createMain(result, tab, dataSets, child);
            }
        }
    }

    private void createTabButton(CompositeMap parent,
                                 List<CompositeMap> tabButtonResult, CompositeMap tabConfig) {
        CompositeMap hBox = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "hBox");
        for (CompositeMap tabButtonChild : tabButtonResult) {
            CompositeMap button = null;
            if (in(tabButtonChild.getString("button_code"), "TAB_REF_SAVE", "TAB_REF_ADD")) {
                CompositeMap findResult = new CompositeMap();
                findResult.putAll(tabButtonChild);
                findResult.putAll(tabConfig);
                String tabButtonScript = ServerLayoutScript.getParserScript(
                        findResult, tabButtonChild.getString("button_code"),
                        this.params);
                this.viewScript.setText(this.viewScript.getText()
                        + tabButtonScript);
            }
            String clickText1 = "${/parameter/@layout_code}_"
                    + tabButtonChild.getString("tab_code") + '_'
                    + tabButtonChild.getString("button_code")
                    + "_layout_dynamic_tab_click";
            String clickText2 = bp_seq + layout_code + "_"
                    + tabButtonChild.getString("tab_code") + "_"
                    + tabButtonChild.getString("button_code")
                    + "_layout_dynamic_tab_click";
            String clickText3 = bp_seq + "${/parameter/@layout_code}_"
                    + tabButtonChild.getString("tab_code") + '_'
                    + tabButtonChild.getString("button_code")
                    + "_layout_dynamic_tab_click";
            if (this.viewScript.getText().indexOf(clickText1) != -1
                    || this.viewScript.getText().indexOf(clickText2) != -1
                    || this.viewScript.getText().indexOf(clickText3) != -1) {
                button = hBox.createChild(parent.getPrefix(),
                        parent.getNamespaceURI(), "button");
                button.put("text", tabButtonChild.getString("prompt"));
                button.put("click", clickText1);
            }
        }
    }

    private CompositeMap createDataSet(CompositeMap dataSets,
                                       CompositeMap dataSetProperty, String base_table,
                                       String parent_table, String configFlag, String base_table_pk) {
        CompositeMap dataSet = dataSets.createChild(dataSets.getPrefix(),
                dataSets.getNamespaceURI(), "dataSet");
        dataSet.putAll(dataSetProperty);
        if (dataSet.get("id") != null) {
            String dsid = dataSet.getString("id");
            if ("N".equals(configFlag)
                    && (parent_table == null || base_table.equals(parent_table))) {
                String rootDataSetList = "window['" + bp_seq + layout_code
                        + "_rootDataSetList']";
                this.viewScript.setText(this.viewScript.getText()
                        + rootDataSetList + ".push('" + dsid + "');\n");
                if (base_table_pk != null) {
                    String layoutDataSetObj = "window['" + bp_seq + layout_code
                            + "_layoutDataSetObj']";
                    this.viewScript.setText(this.viewScript.getText()
                            + layoutDataSetObj + "['" + dsid + "']='"
                            + base_table_pk + "';\n");
                }
            }
            String layoutDataSetList = "window['" + bp_seq + layout_code
                    + "_layoutDataSetList']";
            this.viewScript.setText(this.viewScript.getText()
                    + layoutDataSetList + ".push('" + dsid + "');\n");
        }
        return dataSet;
    }

    private void createScript(CompositeMap dataSets, String functionName) {
        CompositeMap script = dataSets.createChild("script");
        script.setText(functionName + "();");
    }

    private Boolean createTabChild(CompositeMap component,
                                   CompositeMap dataSets, CompositeMap parentTabConfig)
            throws Exception {
        Map<String, Object> parametesTab = new HashMap<String, Object>();
        parametesTab.put("layout_code", layout_code);
        parametesTab.put("function_code", function_code);
        parametesTab.put("parent_tab_code",
                parentTabConfig.getString("tab_code"));
        parametesTab.put("enabled_flag", "Y");
        CompositeMap tabChildResultMap = selectLayoutTabOrTree(parametesTab);
//		CompositeMap tabChildResultMap = queryData(tabConfigImport, parametesTab);
        List<CompositeMap> tabChildResult = tabChildResultMap.getChilds();
        if (tabChildResult != null) {
            createMain(tabChildResult, component, dataSets, parentTabConfig);
            return true;
        } else {
            return false;
        }
    }

    private void createSwitchCard(CompositeMap child, CompositeMap parent,
                                  CompositeMap dataSets) throws Exception {
        CompositeMap switchCard = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "switchCard");
        CompositeMap switchCardParameters = new CompositeMap();
        switchCardParameters.put("layout_code", layout_code);
        switchCardParameters.put("switchcard_tab_code", child.getString("tab_code"));
        switchCardParameters.put("enabled_flag", "Y");

        CompositeMap switchCardFieldResultMap = serverLayoutService.selectLayoutConfigField(prepareParam(switchCardParameters));
//        CompositeMap switchCardFieldResultMap = queryData(fieldConfigImport, switchCardParameters);
        if (switchCardFieldResultMap != null) {
            List<CompositeMap> switchCardFieldResult = switchCardFieldResultMap
                    .getChilds();
            if (switchCardFieldResult != null) {
                for (CompositeMap switchCardFieldChild : switchCardFieldResult) {
                    CompositeMap switchCardBinder = CompositeUtil.findChild(
                            this.enableTabResultMap, "record", "tab_code",
                            switchCardFieldChild.getString("tab_code"));
                    if (switchCardBinder != null) {
                        switchCard.put("name",
                                switchCardFieldChild.getString("column_name"));
                        switchCard
                                .put("bindtarget",
                                        bp_seq
                                                + layout_code
                                                + "_"
                                                + switchCardBinder
                                                .getString("form_binder_tab_code")
                                                + "_"
                                                + switchCardBinder
                                                .getString("base_table")
                                                + "_ds");
                    }
                }
            }
        }
        CompositeMap parametersNew = new CompositeMap();
        parametersNew.put("layout_code", layout_code);
        parametersNew.put("parent_tab_code", child.getString("tab_code"));

        CompositeMap switchCardValueResultMap = serverLayoutService.selectLayoutSwitchCardValue(parametersNew);

        parametersNew.put("function_code", function_code);
        parametersNew.put("enabled_flag", "Y");
        CompositeMap switchCardResultMap = selectLayoutTabOrTree(parametersNew);
//		CompositeMap switchCardResultMap = queryData(tabConfigImport, parametersNew);

//        CompositeMap switchCardValueResultMap = queryData( switchcardValueConfigImport, parametersNew);
        if (switchCardResultMap != null && switchCardValueResultMap != null) {
            List<CompositeMap> result = switchCardResultMap.getChilds();
            List<CompositeMap> switchValueResult = switchCardValueResultMap.getChilds();
            if (result != null && switchValueResult != null) {
                CompositeMap cards = switchCard.createChild(parent.getPrefix(),
                        parent.getNamespaceURI(), "cards");
                String switchCardScript = ServerLayoutScript.getParserScript(
                        child, child.getString("tab_type"), this.params);
                this.viewScript.setText(this.viewScript.getText()
                        + switchCardScript);
                CompositeMap switchEvents = switchCard.createChild(p_, ns_, "events");
                CreateEvent(switchEvents, "cardhide", "ON_DOC_LAYOUT_CARDHIDE");
                CreateEvent(switchEvents, "cardshow", "ON_DOC_LAYOUT_CARDSHOW");
                for (CompositeMap cardValueChild : switchValueResult) {
                    String switchcard_value = cardValueChild.getString(
                            "switchcard_value", "");
                    List<CompositeMap> cardResult = new ArrayList<CompositeMap>();
                    for (CompositeMap cardChild : result) {
                        if (switchcard_value.equals(cardChild
                                .getString("switchcard_value"))) {
                            cardResult.add(cardChild);
                        }
                    }
                    CompositeMap card = cards.createChild(parent.getPrefix(),
                            parent.getNamespaceURI(), "card");
                    card.put("value", switchcard_value);
                    card.put("hidden", "false");
                    createMain(cardResult, card, dataSets, child);
                }
            }
        }
    }

    private void createAccordionPanel(CompositeMap child, CompositeMap parent,
                                      CompositeMap dataSets) throws Exception {
        CompositeMap accordionPanel = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "accordionPanel");
        accordionPanel.put("width", child.get("width"));
        accordionPanel.put("height", child.get("height"));
        accordionPanel.put("marginwidth", child.get("margin_width"));
        accordionPanel.put("margin_height", child.get("margin_height"));
        accordionPanel.put("singlemode", "false");
        CompositeMap accordions = accordionPanel.createChild(
                parent.getPrefix(), parent.getNamespaceURI(), "accordions");
        CompositeMap accordionPanelParameters = new CompositeMap();
        accordionPanelParameters.put("layout_code", layout_code);
        accordionPanelParameters.put("parent_tab_code",
                child.getString("tab_code"));
        accordionPanelParameters.put("enabled_flag", "Y");

        CompositeMap accordionPanelResultMap = selectLayoutTabOrTree(accordionPanelParameters);
//		CompositeMap accordionPanelResultMap = queryData(tabConfigImport, accordionPanelParameters);
        if (accordionPanelResultMap != null) {
            List<CompositeMap> accordionPanelResult = accordionPanelResultMap.getChilds();
            if (accordionPanelResult != null) {
                createMain(accordionPanelResult, accordions, dataSets, child);
            }
        }
    }

    private void createAccordion(CompositeMap child, CompositeMap parent,
                                 CompositeMap dataSets) throws Exception {
        CompositeMap accordion = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "accordion");
        accordion.put("prompt", child.getString("tab_desc"));
        accordion.put("selected", "false");
        CompositeMap accordionParameters = new CompositeMap();
        accordionParameters.put("layout_code", layout_code);
        accordionParameters.put("parent_tab_code", child.getString("tab_code"));
        accordionParameters.put("enabled_flag", "Y");

        CompositeMap accordionResultMap = selectLayoutTabOrTree(accordionParameters);
//		CompositeMap accordionResultMap = queryData(tabConfigImport, accordionParameters);
        if (accordionResultMap != null) {
            List<CompositeMap> accordionResult = accordionResultMap.getChilds();
            if (accordionResult != null) {
                createMain(accordionResult, accordion, dataSets, child);
            }
        }
    }

    private void createHbox(CompositeMap child, CompositeMap parent,
                            CompositeMap dataSets) throws Exception {
        CompositeMap hBox = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "hBox");
        CompositeMap hBoxParameters = new CompositeMap();
        hBoxParameters.put("layout_code", layout_code);
        hBoxParameters.put("parent_tab_code", child.getString("tab_code"));
        hBoxParameters.put("enabled_flag", "Y");
        CompositeMap hBoxResultMap = selectLayoutTabOrTree(hBoxParameters);
//		CompositeMap hBoxResultMap = queryData(tabConfigImport, hBoxParameters);
        if (hBoxResultMap != null) {
            List<CompositeMap> hBoxResult = hBoxResultMap.getChilds();
            if (hBoxResult != null) {
                createMain(hBoxResult, hBox, dataSets, child);
            }
        }
    }

    private void createVbox(CompositeMap child, CompositeMap parent,
                            CompositeMap dataSets) throws Exception {
        CompositeMap vBox = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "vBox");
        CompositeMap vBoxParameters = new CompositeMap();
        vBoxParameters.put("layout_code", layout_code);
        vBoxParameters.put("parent_tab_code", child.getString("tab_code"));
        vBoxParameters.put("enabled_flag", "Y");
        CompositeMap vBoxResultMap = selectLayoutTabOrTree(vBoxParameters);
//		CompositeMap vBoxResultMap = queryData(tabConfigImport, vBoxParameters);
        if (vBoxResultMap != null) {
            List<CompositeMap> vBoxResult = vBoxResultMap.getChilds();
            if (vBoxResult != null) {
                createMain(vBoxResult, vBox, dataSets, child);
            }
        }
    }

    private void createQueryForm(CompositeMap child, CompositeMap parent,
                                 CompositeMap dataSets) throws Exception {
        CompositeMap queryForm = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "queryForm");
        CompositeMap queryFormParameters = new CompositeMap();
        queryForm.put("createsearchbutton", "false");
        queryForm.put("id",
                bp_seq + layout_code + "_" + child.getString("tab_code")
                        + "_id");
        queryFormParameters.put("layout_code", layout_code);
        queryFormParameters.put("parent_tab_code", child.getString("tab_code"));
        queryFormParameters.put("enabled_flag", "Y");
        CompositeMap queryFormResultMap = selectLayoutTabOrTree(queryFormParameters);
//		CompositeMap queryFormResultMap = queryData(tabConfigImport, queryFormParameters);
        if (queryFormResultMap != null) {
            List<CompositeMap> queryFormResult = queryFormResultMap.getChilds();
            if (queryFormResult != null) {
                createMain(queryFormResult, queryForm, dataSets, child);
            }
        }
    }

    private void createFormBody(CompositeMap child, CompositeMap parent, CompositeMap dataSets) throws Exception {
        CompositeMap formBody = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "formBody");
        CompositeMap formBodyParameters = new CompositeMap();
        formBodyParameters.put("layout_code", layout_code);
        formBodyParameters.put("parent_tab_code", child.getString("tab_code"));
        formBodyParameters.put("enabled_flag", "Y");
        CompositeMap formBodyResultMap = selectLayoutTabOrTree(formBodyParameters);
//		CompositeMap formBodyResultMap = queryData(tabConfigImport, formBodyParameters);
        if (formBodyResultMap != null) {
            List<CompositeMap> formBodyResult = formBodyResultMap.getChilds();
            if (formBodyResult != null) {
                createMain(formBodyResult, formBody, dataSets, child);
            }
        }
    }

    private void createDocumentHistoryDataSet(CompositeMap child,
                                              CompositeMap dataSets){
        Long documentId = params.getLong("document_id");
        String documentCategory = params.getString("document_category");
        String querySource = child.getString("query_source");
        if (querySource.startsWith("$")) {
            // 从sqlId获取
            querySource = querySource.substring(1);
        } else if (querySource.startsWith("/")) {
            querySource = querySource;
        }
        String dataSetId = bp_seq + layout_code + "_"
                + child.getString("tab_code") + "_"
                + child.getString("base_table") + "_show_document_history_ds";
        CompositeMap dataSetProperty = new CompositeMap();
        CompositeMap dataSet = null;
        dataSetProperty.put("id", dataSetId);
        dataSetProperty.put("fetchall", "true");
//        dataSetProperty.put("queryurl", "${/request/@context_path}/autocrud/layout.server_doc_layout_show_history/query?table_name="  + child.getString("base_table").toUpperCase());
        dataSetProperty.put("queryurl", "${/request/@context_path}/hls/doc/layout/document/history/query?tableName=" + child.getString("base_table").toUpperCase()+"&documentId="+documentId+"&documentCategory="+documentCategory+"&querySource="+querySource);
        dataSet = createDataSet(dataSets, dataSetProperty,
                child.getString("base_table"), child.getString("parent_table"),
                "Y", child.getString("base_table_pk"));
        CompositeMap tempDataSetEvents = dataSet.createChild(
                dataSet.getPrefix(), dataSet.getNamespaceURI(),
                "events");
        CreateEvent(tempDataSetEvents, "query", "window[" + "'" + bp_seq
                + layout_code + "_" + child.getString("tab_code")
                + "_ON_LAYOUT_DYNAMIC_INNER_DOCUMENT_HISTORY_QUERY" + "']");
    }

    private void createShowHistoryDataSet(CompositeMap child,
                                          CompositeMap dataSets) {
        String dataSetId = bp_seq + layout_code + "_"
                + child.getString("tab_code") + "_"
                + child.getString("base_table") + "_show_history_ds";
        CompositeMap dataSetProperty = new CompositeMap();
        CompositeMap dataSet = null;
        dataSetProperty.put("id", dataSetId);
        dataSetProperty.put("fetchall", "true");
//        dataSetProperty.put("queryurl", "${/request/@context_path}/autocrud/layout.server_doc_layout_show_history/query?table_name="  + child.getString("base_table").toUpperCase());
        dataSetProperty.put("queryurl", "${/request/@context_path}/hls/doc/layout/history/query?table_name=" + child.getString("base_table").toUpperCase());
        dataSet = createDataSet(dataSets, dataSetProperty,
                child.getString("base_table"), child.getString("parent_table"),
                "Y", child.getString("base_table_pk"));

    }

    @SuppressWarnings("unchecked")
    private void createComponent(CompositeMap child, CompositeMap parent,
                                 CompositeMap dataSets) throws Exception {
        String tab_type = (String) child.get("tab_type");
        if ("SWITCHCARD".equals(tab_type)) {
            createSwitchCard(child, parent, dataSets);
            return;
        } else if ("FIELDBOX".equals(tab_type)) {
            createFieldBox(child, parent, dataSets);
            return;
        } else if ("ACCORDIONPANEL".equals(tab_type)) {
            createAccordionPanel(child, parent, dataSets);
            return;
        } else if ("ACCORDION".equals(tab_type)) {
            createAccordion(child, parent, dataSets);
            return;
        } else if ("HBOX".equals(tab_type)) {
            createHbox(child, parent, dataSets);
            return;
        } else if ("VBOX".equals(tab_type)) {
            createVbox(child, parent, dataSets);
            return;
        } else if ("QUERYFORM".equals(tab_type)) {
            createQueryForm(child, parent, dataSets);
            return;
        }
        CompositeMap finalComponent = null;
        CompositeMap editors = null;
        CompositeMap dataSetProperty = new CompositeMap();
        String base_table = child.getString("base_table", "");
        String tab_code = child.getString("tab_code");
        String tabReloadFlag = "N";
        dataSetProperty.put("id", bp_seq + layout_code + "_" + tab_code + "_"
                + base_table + "_ds");
        CompositeMap dataSet = null;
        ServiceOption so = ServiceOption.createInstance();
        ServiceOption so1 = ServiceOption.createInstance();
        so.setDefaultWhereClause("t1.enabled_flag='Y'");
        Map<String, Object> cptParameters = new HashMap<String, Object>();
        cptParameters.put("layout_code", layout_code);
        cptParameters.put("tab_code", tab_code);
        if (in(tab_type, "FORM", "GRIDBOX", "FORMTOOLBAR", "FORMBODY")) {
            so1.setDefaultWhereClause("t1.enabled_flag(+)='Y'");
        } else if (in(tab_type, "GRID", "SINGLE_GRID", "TABLE", "TEXTAREA", "ATTACH", "FIELDBOXCOLUMN")) {
            so1.setDefaultWhereClause("t1.enabled_flag='Y' and t1.display_flag='Y'");
        }


        Map<String, Object> params = prepareParam(cptParameters);
        params.put("leaf_default_where_sql", "t1.enabled_flag='Y' and t1.display_flag='Y'");
        CompositeMap fieldResultMap = serverLayoutService.selectLayoutConfigField(params);
//        CompositeMap fieldResultMap = queryData(fieldConfigImport, cptParameters, so);
        if (fieldResultMap != null) {
            CompositeMap configDataSetProperty = new CompositeMap();
            CompositeMap lovMappingDataSetProperty = new CompositeMap();
            lovMappingDataSetProperty.put("id", bp_seq + layout_code + "_"
                    + tab_code + "_lov_getmapping_ds");
            lovMappingDataSetProperty.put("fetchall", "true");
//            lovMappingDataSetProperty.put("queryurl", "${/request/@context_path}/modules/layout/server_layout_sql_parameters_load.lsc");
            lovMappingDataSetProperty.put("queryurl", "${/request/@context_path}/hls/doc/layout/config/field");
            CompositeMap lovMappingDataSet = createDataSet(dataSets,
                    lovMappingDataSetProperty, base_table,
                    child.getString("parent_table"), "Y", null);
            CompositeMap configDataSet = null;
            if ("N".equals(child.getString("parent_tab_code_flag"))
                    && in(tab_type, new Object[]{"FORM", "TEXTAREA",
                    "FIELDBOXCOLUMN", "FORMTOOLBAR", "FORMBODY"})) {
                configDataSet = CompositeUtil.findChild(
                        dataSets,
                        "dataSet",
                        "id",
                        bp_seq + layout_code + "_"
                                + child.getString("parent_table_tab_code")
                                + "_con_layout_config_ds");
            }
            if (configDataSet == null) {
                configDataSetProperty.put("id", bp_seq + layout_code + "_"
                        + tab_code + "_con_layout_config_ds");
                configDataSet = createDataSet(dataSets, configDataSetProperty,
                        base_table, child.getString("parent_table"), "Y", null);
                fieldResultMap.setName("configResult");
                this.svcContext.getModel().addChild(fieldResultMap);
            }
            List<CompositeMap> fieldResult = fieldResultMap.getChilds();
            if (fieldResult != null) {
                CompositeMap datas = null;
                datas = CompositeUtil.findChild(configDataSet, "datas");
                if (datas == null) {
                    datas = configDataSet.createChild(
                            configDataSet.getPrefix(),
                            configDataSet.getNamespaceURI(), "datas");
                }
                for (CompositeMap fieldChild : fieldResult) {
                    CompositeMap datasRecord = datas.createChild(
                            datas.getPrefix(), datas.getNamespaceURI(),
                            "record");
                    datasRecord.putAll(fieldChild);
                }
            }
        }

        String tempTreeFlag = "N";
        if (child.getString("base_table") == null
                && child.getString("parent_table") == null) {
            dataSet = createDataSet(dataSets, dataSetProperty,
                    child.getString("base_table"),
                    child.getString("parent_table"), "N", null);
            dataSet.put("autocreate", "true");
        } else {
            if ("N".equals(child.getString("parent_tab_code_flag"))
                    && in(tab_type, new Object[]{"FORM", "TEXTAREA",
                    "FIELDBOXCOLUMN", "FORMTOOLBAR", "FORMBODY"})) {
                dataSet = CompositeUtil.findChild(
                        dataSets,
                        "dataSet",
                        "id",
                        bp_seq + layout_code + "_"
                                + child.getString("parent_table_tab_code")
                                + "_" + child.getString("base_table") + "_ds");
                tabReloadFlag = "Y";
                if (dataSet == null) {
                    tempTreeFlag = "Y";
                    tabReloadFlag = "N";
                    dataSet = dataSets.createChild(dataSets.getPrefix(),
                            dataSets.getNamespaceURI(), "dataSet");
                    dataSet.put("id", "temp_ds");
                    dataSet.put("processfunction", bp_seq + layout_code + "_"
                            + tab_code + "_temp_processfunction");
                    CompositeMap datas = dataSet.createChild(
                            dataSet.getPrefix(), dataSet.getNamespaceURI(),
                            "datas");
                    CompositeMap record = datas.createChild(datas.getPrefix(),
                            datas.getNamespaceURI(), "record");
                    record.put("name", "temp");
                    CompositeMap tempDataSetEvents = dataSet.createChild(
                            dataSet.getPrefix(), dataSet.getNamespaceURI(),
                            "events");
                    CreateEvent(tempDataSetEvents, "add", "temp_add");
                    createScript(dataSets, "temp_create");
                    String tempTreeScript = ServerLayoutScript.getParserScript(
                            child, "TEMPTREE", this.params);
                    this.viewScript.setText(this.viewScript.getText()
                            + tempTreeScript);
                }
            } else {
                dataSet = createDataSet(dataSets, dataSetProperty,
                        child.getString("base_table"),
                        child.getString("parent_table"), "N",
                        child.getString("base_table_pk"));
                if ("Y".equals(child.getString("parent_table_flag"))) {
                    createScript(dataSets, "window['" + bp_seq + layout_code
                            + "_" + tab_code + "_CHILD_ONREADY']");
                } else {
                    createScript(dataSets, "window['" + bp_seq + layout_code
                            + "_" + tab_code + "_ONREADY']");

                }
            }
        }
        if ("Y".equals(this.params.getString("show_history_flag"))) {
            createShowHistoryDataSet(child, dataSets);
        }
        if("Y".equals(this.params.getString("show_document_history_flag"))){
            createDocumentHistoryDataSet(child,dataSets);
        }
        CompositeMap dataSetEvents = null;
        if ("N".equals(tempTreeFlag)) {
            if (child.getString("base_table") != null
                    && child.getString("parent_table") == null) {
                dataSet.put("bindtarget", bp_seq + layout_code + "_virtual_ds");
            }
            //不应再有ATTACH类型，换成列表
            if ("ATTACH".equals(tab_type)) {
//                dataSet.put("selectfunction", "server_layout_cdd_selectFunc");


//                dataSet.put("model", "layout.server_layout_doc_cdd_item");
//                dataSet.put("submiturl"."${/request/@context_path}/autocrud/layout.server_layout_doc_cdd_item/submit");
//                dataSet.put(
//                        "queryurl",
//                        "${/request/@context_path}/autocrud/layout.server_layout_doc_cdd_item/query?attachment_tab_group="
//                                + child.getString("attachment_tab_group", "")
//                                + "&document_category=${/parameter/@document_category}");
            } else {
                if ("SINGLE_GRID".equals(tab_type)) {
                    dataSet.put("selectionmodel", "single");
                }
                String query_bp_seq = null;
                if ((this.params.getString("tree_code") == null && in(
                        child.getString("query_bp_category", ""), "TENANT", "GUARANTOR", "TENANT_SEC"))) {
                    // query_bp_seq = "1";
                } else if ((this.params.getString("tree_code") != null && !in(
                        child.getString("query_bp_category", ""), "TENANT", "GUARANTOR", "TENANT_SEC"))) {
                    // query_bp_seq = bp_seq;
                } else {
                    query_bp_seq = bp_seq;
                }
//                拿到原始的数据来源
                String querySource = child.getString("query_source", "");
                boolean queryFromController = false;
                boolean queryFromSqlId = false;
                String sqlOrController = null;
                if (querySource.startsWith("$")) {
                    // 从sqlId获取
                    queryFromSqlId = true;
                    sqlOrController = "/layout/common/source?sqlId=" + querySource.substring(1);
                } else if (querySource.startsWith("/")) {
                    queryFromController = true;
                    sqlOrController = querySource;
                } else if(querySource.startsWith("#")) {
                    queryFromController = true;
                    sqlOrController = "/metadata/query/data?test=123&metadataId=" + querySource.substring(1);
                } else {
                    queryFromSqlId = true;
                    sqlOrController = "/layout/common/source?layout_code=" + layout_code + "&tab_code=" + tab_code;
                }
                // targetUrl 不应该是 "/modules/layout/server_layout_base_query.lsc" ，如果是，报错
                String targetUrl = queryFromController || queryFromSqlId ?
                        sqlOrController : "/modules/layout/server_layout_base_query.lsc";

                String queryUrl = str("${/request/@context_path}",
                        targetUrl,
                        targetUrl.indexOf('?') > 0 ? "&" : "?",
                        "layout_code=${/parameter/@layout_code}&tab_code=",
                        tab_code);
//                String queryUrl = "${/request/@context_path}/modules/layout/server_layout_base_query.lsc?layout_code=${/parameter/@layout_code}&tab_code=" + tab_code;

                if (query_bp_seq != null) {
                    queryUrl = queryUrl + "&bp_seq=" + query_bp_seq;
                }
                if (child.getString("query_bp_category") != null) {
                    queryUrl = queryUrl + "&bp_category="
                            + child.getString("query_bp_category");
                }
                if (this.params.getString("tree_code") != null) {
                    queryUrl = queryUrl + "&tree_code="
                            + this.params.getString("tree_code");
                }
                String document_category = this.params
                        .getString("document_category");
                String document_type = this.params.getString("document_type");

                if (document_category != null && document_category.length() > 0) {
                    queryUrl = queryUrl + "&document_category="
                            + document_category;
                }
                if (document_type != null && document_type.length() > 0) {
                    queryUrl = queryUrl + "&document_type=" + document_type;
                }

                if (queryFromController || (child.getString("query_source") != null && dataSet.getString("queryUrl") == null)) {
                    dataSet.put("queryurl", queryUrl);
                }

                if (child.getString("base_table") != null
                        && dataSet.getString("submiturl") == null) {
                    dataSet.put(
                            "submiturl",
//                            "${/request/@context_path}/modules/layout/server_layout_common_save.lsc?base_table="
                            "${/request/@context_path}/server/layout/save?base_table="
                                    + base_table
                                    + "&query_only="
                                    + child.getString("query_only", "")
                                    + "&tab_code="
                                    + tab_code
                                    + "&layout_code="
                                    + layout_code
                                    + "&document_id=${/parameter/@document_id}&document_category=${/parameter/@document_category}&document_type=${/parameter/@document_type}&function_code=${/parameter/@function_code}&function_usage=${/parameter/@function_usage}&parent_table="
                                    + child.getString("parent_table", "")
                                    + "&parent_base_table_pk="
                                    + child.getString("parent_base_table_pk",
                                    "")
                                    + "&root_tree_code="
                                    + child.getString("root_tree_code", "")
                                    + "&number_of_tenant=${/parameter/@number_of_tenant}&number_of_guarantor=${/parameter/@number_of_guarantor}&number_of_actual_controller=${/parameter/@number_of_actual_controller}&number_of_quotation=${/parameter/@number_of_quotation}");
                }
            }
            if ("N".equals(tabReloadFlag)) {
                dataSetEvents = dataSet.createChild(dataSet.getPrefix(),
                        dataSet.getNamespaceURI(), "events");
            } else {
                dataSetEvents = dataSet.getChild("events");
            }
        }
        if (tab_type != null) {
            CompositeMap component = null;
            Map<String, Object> tabButtonQueryParameters = new HashMap<>();
            tabButtonQueryParameters.put("layout_code", layout_code);
            tabButtonQueryParameters.put("function_code", function_code);
            tabButtonQueryParameters.put("tab_code", child.getString("tab_code"));
            tabButtonQueryParameters.put("enabled_flag", "Y");

            CompositeMap tabButtonResultMap = serverLayoutService.selectLayoutTabButton(tabButtonQueryParameters);
//            CompositeMap tabButtonResultMap = queryData(tabButtonConfigImport, tabButtonQueryParameters);
            List<CompositeMap> tabButtonResult = null;
            if (tabButtonResultMap != null) {
                tabButtonResult = tabButtonResultMap.getChilds();
                if (tabButtonResult != null
                        && !in(tab_type, new Object[]{"GRID", "SINGLE_GRID",
                        "TABLE", "ATTACH", "GRIDBOX"})) {
                    createTabButton(parent, tabButtonResult, child);
                }
            }
            if ("TEXTAREA".equals(tab_type)) {
                component = parent.createChild(parent.getPrefix(),
                        parent.getNamespaceURI(), "hBox");
                component.put("labelseparator", " ");
            } else if (in(tab_type, new Object[]{"SINGLE_GRID", "ATTACH"})) {
                component = parent.createChild(parent.getPrefix(),
                        parent.getNamespaceURI(), "grid");
            } else if ("GRIDBOX".equals(tab_type)) {
                component = parent.createChild(parent.getPrefix(),
                        parent.getNamespaceURI(), "gridBox");
                if ("Y".equals(child.getString("query_only"))
                        || "QUERY".equals(this.params.getString("function_usage"))
                        || "READONLY".equals(this.params.getString("maintain_type"))) {
                    component.put("classname", "layout_gridbox_button_hide");
                }
                component.put("title", child.getString("tab_desc"));
                component.put("column", child.getString("column_count", "1"));
                component.put("row", child.getString("row_count", "1"));
                component.put("labelwidth", child.get("label_width"));
            } else if ("FIELDBOXCOLUMN".equals(tab_type)) {
                component = parent.createChild(parent.getPrefix(),
                        parent.getNamespaceURI(), "fieldBoxColumn");
            } else if ("FORMTOOLBAR".equals(tab_type)) {
                component = parent.createChild(parent.getPrefix(),
                        parent.getNamespaceURI(), "formToolBar");
            } else if ("FORMBODY".equals(tab_type)) {
                component = parent;
            } else {
                component = parent.createChild(parent.getPrefix(),
                        parent.getNamespaceURI(), tab_type.toLowerCase());
            }
            if (component.get("id") == null) {
                component.put("id", layout_code + "_" + tab_code + "_component_id");
            }
            if (("Y".equals(this.params.getString("show_history_flag"))
                    && (in(tab_type, new Object[]{"FORM", "FIELDBOXCOLUMN",
                    "TEXTAREA", "FORMTOOLBAR", "FORMBODY"}) && "Y"
                    .equals(tabReloadFlag)) || "Y".equals(tempTreeFlag))) {
                String tabReloadScript = ServerLayoutScript.getParserScript(
                        child, "TABRELOAD", this.params);
                this.viewScript.setText(this.viewScript.getText()
                        + tabReloadScript);
                CompositeMap reloadScript = parent.createChild(
                        parent.getPrefix(), parent.getNamespaceURI(), "script");
                String functionName = "window[" + "'" + bp_seq + layout_code
                        + "_" + tab_code + "_TABRELOAD_SHOW_HISTORY" + "']";
                reloadScript.setText(functionName + "();");
            }
            if (in(tab_type, new Object[]{"FORM", "FIELDBOXCOLUMN",
                    "FORMBODY", "FORMTOOLBAR"})) {
                if ("N".equals(tempTreeFlag)) {
                    String formScript = ServerLayoutScript.getParserScript(
                            child, "FORM", this.params);
                    this.viewScript.setText(this.viewScript.getText()
                            + formScript);
                    if ("N".equals(tabReloadFlag)) {
                        CreateEvent(dataSetEvents, "submitfailed", "window["
                                + "'" + bp_seq + layout_code
                                + "_ON_LAYOUT_DYNAMIC_INNER_SUBMITFAILED"
                                + "']");
                        CreateEvent(dataSetEvents, "add", "window[" + "'"
                                + bp_seq + layout_code + "_" + tab_code
                                + "_ON_LAYOUT_DYNAMIC_INNER_FORM_INIT" + "']");
                        CreateEvent(dataSetEvents, "load", "window[" + "'"
                                + bp_seq + layout_code + "_" + tab_code
                                + "_ON_LAYOUT_DYNAMIC_INNER_FORM_INIT" + "']");
                        CreateEvent(dataSetEvents, "query", "window[" + "'"
                                + bp_seq + layout_code + "_" + tab_code
                                + "_ON_LAYOUT_DYNAMIC_INNER_FORM_QUERY" + "']");
                    }
                    dataSet.put("fetchall", child.getString("fetchall"));
                    if (dataSet.getString("processfunction") == null) {
                        dataSet.put("processfunction", bp_seq + layout_code
                                + "_" + tab_code + "_CON_FORM_PROCESSFUNCTION");
                    }
                    if (child.getString("base_table") != null
                            && dataSet.getString("bindname") == null) {
                        dataSet.put("bindname", bp_seq + layout_code + "_"
                                + tab_code + "_" + base_table);
                    }
                    if ("Y".equals(child.getString("parent_table_flag"))
                            && child.getString("query_tab_code") == null
                            && child.getString("base_table") != null
                            && dataSet.getString("bindtarget") == null) {
                        dataSet.put(
                                "bindtarget",
                                child.getString("repeat_bp_seq", "")
                                        + layout_code
                                        + "_"
                                        + child.getString("parent_table_tab_code")
                                        + "_" + child.getString("parent_table")
                                        + "_ds");
                    }
                    if (child.getString("query_tab_code") != null) {
                        dataSet.put("querydataset", bp_seq + layout_code + "_"
                                + child.getString("query_tab_code") + "_"
                                + child.getString("query_base_table", "")
                                + "_ds");
                    }
                } else {
                    String crossTreeformScript = ServerLayoutScript
                            .getParserScript(child, "CROSSTREEFORM",
                                    this.params);
                    this.viewScript.setText(this.viewScript.getText()
                            + crossTreeformScript);
                }
                if (in(tab_type, new Object[]{"FORM", "FORMTOOLBAR",
                        "FORMBODY"})) {
                    component.put("title", child.get("tab_desc"));
                    component.put("marginwidth", child.get("margin_width"));
                    component.put("width", child.get("width"));
                    component.put("wrapperadjust", "false");
                    if (child.get("flexible") != null) {
                        component.put("flexible", child.get("flexible"));
                    }
                    if ("Y".equals(child.get("show_box"))) {
                        CompositeMap div = component.createChild("div");
                        div.put("style", "width:" + child.get("box_width")
                                + "px");
                        CompositeMap box = div.createChild(parent.getPrefix(),
                                parent.getNamespaceURI(), "box");
                        finalComponent = box;
                        box.put("column", child.get("column_count"));
                        box.put("labelseparator", " ");
                        box.put("labelwidth", child.get("label_width"));
                        box.put("row", child.get("row_count"));
                        box.put("width", child.get("box_width"));
                    } else {
                        finalComponent = component;
                        component.put("column", child.get("column_count"));
                        component.put("labelseparator", " ");
                        component.put("labelwidth", child.get("label_width"));
                        component.put("row", child.get("row_count"));
                    }
                } else {
                    finalComponent = component;
                    component.put("title", child.get("tab_desc"));
                    component.put("marginwidth", child.get("margin_width"));
                    component.put("width", child.get("width"));
                    component.put("wrapperadjust", "false");
                    component.put("fieldwidth", child.get("field_width"));
                    component.put("labelwidth", child.get("label_width"));
                }

                if ("FORM".equals(tab_type)
                        && createTabChild(component, dataSets, child)) {
                    return;
                }

            } else if (in(tab_type, new Object[]{"GRID", "SINGLE_GRID",
                    "TABLE", "GRIDBOX"})) {
                String gridScript = ServerLayoutScript.getParserScript(child,
                        "GRID", this.params);
                this.viewScript.setText(this.viewScript.getText() + gridScript);
                CreateEvent(dataSetEvents, "beforecreate", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_BEFORECREATE" + "']");
                CreateEvent(dataSetEvents, "add", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_ADD" + "']");
                CreateEvent(dataSetEvents, "load", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_LOAD" + "']");
                CreateEvent(dataSetEvents, "indexchange", "window[" + "'"
                        + bp_seq + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_INDEXCHANGE" + "']");
                CreateEvent(dataSetEvents, "query", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_QUERY" + "']");
                CreateEvent(dataSetEvents, "select", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_SELECT" + "']");
                CreateEvent(dataSetEvents, "unselect", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_UNSELECT" + "']");
                CreateEvent(dataSetEvents, "remove", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_REMOVE" + "']");
                CreateEvent(dataSetEvents, "beforeremove", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_BEFORE_REMOVE" + "']");
                if (child.getString("page_size") != null) {
                    dataSet.put("page_size", child.getString("page_size"));
                } else {
                    dataSet.put("autopagesize", "true");
                }
                dataSet.put("fetchall", child.getString("fetchall"));
                if (dataSet.getString("bindtarget") == null
                        && child.getString("base_table") != null
                        && child.getString("parent_table") != null) {
                    dataSet.put(
                            "bindtarget",
                            child.getString("repeat_bp_seq", "") + layout_code
                                    + "_"
                                    + child.getString("parent_table_tab_code")
                                    + '_' + child.getString("parent_table")
                                    + "_ds");
                }
                if (child.getString("query_tab_code") != null) {
                    dataSet.put(
                            "querydataset",
                            bp_seq + layout_code + "_"
                                    + child.getString("query_tab_code") + "_"
                                    + child.getString("query_base_table", "")
                                    + "_ds");
                }
                if (dataSet.getString("bindname") == null) {
                    dataSet.put("bindname", bp_seq + layout_code + "_"
                            + tab_code + "_" + base_table);
                }
                dataSet.put("processfunction", bp_seq + layout_code + "_"
                        + tab_code + "_CON_GRID_PROCESS");
                if ("N".equals(child.getString("selected_flag","Y"))) {
                    dataSet.put("selectable", "false");
                } else {
                    dataSet.put("selectable", "true");
                }

                component.put("id", bp_seq + layout_code + "_" + tab_code + '_'
                        + base_table + "_layout_grid_id");
                component.put("bindtarget", bp_seq + layout_code + "_"
                        + tab_code + "_" + base_table + "_ds");
                component.put("marginwidth", child.get("margin_width"));
                component.put("width", child.get("width"));
                component.put("height", child.get("height"));
                component.put("marginheight", child.get("margin_height"));
                component.put("navbar", "true");
                component.put("canwheel", "true");
                if (createTabChild(component, dataSets, child)) {
                    return;
                }
                if (fieldResultMap != null
                        && fieldResultMap.getChilds() != null) {
                    CompositeMap columns = component.createChild(
                            component.getPrefix(), component.getNamespaceURI(),
                            "columns");
                    editors = component.createChild(component.getPrefix(),
                            component.getNamespaceURI(), "editors");
                    finalComponent = columns;
                    CompositeMap toolBar = null;
                    if (child.getString("tab_desc") != null
                            || tabButtonResult != null) {
                        toolBar = component.createChild(component.getPrefix(),
                                component.getNamespaceURI(), "toolBar");
                        if (child.getString("tab_desc") != null) {
                            CompositeMap span = toolBar.createChild("span");
                            span.put("style", "color:#435FBF;font-size:13px");
                            span.setText(child.getString("tab_desc"));
                        }
                        if (tabButtonResult != null) {
                            for (CompositeMap tabButtonChild : tabButtonResult) {
                                if ("Y".equals(child.getString("query_only")) || ("QUERY".equals(this.params.getString("function_usage")) && !"Y".equalsIgnoreCase(tabButtonChild.getString("query_display_flag")))) {
                                    continue;
                                }
                                createGridButton(tabButtonChild, toolBar);
                            }
                        }
                    }
                }
            } else if ("TEXTAREA".equals(tab_type)) {
                if ("N".equals(tempTreeFlag)) {
                    String textAreaScript = ServerLayoutScript.getParserScript(
                            child, "TEXTAREA", this.params);
                    this.viewScript.setText(this.viewScript.getText()
                            + textAreaScript);
                    if ("N".equals(tabReloadFlag)) {
                        CreateEvent(dataSetEvents, "add", "window[" + "'"
                                + bp_seq + layout_code + "_" + tab_code
                                + "_ON_LAYOUT_DYNAMIC_INNER_TEXTAREA_INIT"
                                + "']");
                        CreateEvent(dataSetEvents, "load", "window[" + "'"
                                + bp_seq + layout_code + "_" + tab_code
                                + "_ON_LAYOUT_DYNAMIC_INNER_TEXTAREA_INIT"
                                + "']");
                        CreateEvent(dataSetEvents, "submitfailed", "window["
                                + "'" + bp_seq + layout_code
                                + "_ON_LAYOUT_DYNAMIC_INNER_SUBMITFAILED"
                                + "']");
                    }
                    dataSet.put("fetchall", child.getString("fetchall"));
                    if (child.getString("base_table") != null
                            && dataSet.getString("bindname") == null) {
                        dataSet.put("bindname", bp_seq + layout_code + "_"
                                + tab_code + "_" + base_table);
                    }
                    if (dataSet.getString("processfunction") == null) {
                        dataSet.put("processfunction", bp_seq + layout_code
                                + "_" + tab_code
                                + "_CON_TEXTAREA_PROCESSFUNCTION");
                    }
                    if ("Y".equals(child.getString("parent_table_flag"))
                            && child.getString("query_tab_code") == null
                            && child.getString("base_table") != null
                            && dataSet.getString("bindtarget") == null) {
                        dataSet.put(
                                "bindtarget",
                                child.getString("repeat_bp_seq", "")
                                        + layout_code
                                        + "_"
                                        + child.getString("parent_table_tab_code")
                                        + "_" + child.getString("parent_table")
                                        + "_ds");
                    }
                    if (child.getString("query_tab_code") != null) {
                        dataSet.put("querydataset", bp_seq + layout_code + "_"
                                + child.getString("query_tab_code") + "_"
                                + child.getString("query_base_table", "")
                                + "_ds");
                    }
                }
                finalComponent = component;
            } else if ("ATTACH".equals(tab_type)) {
                String attachScript = ServerLayoutScript.getParserScript(child,
                        "ATTACH", this.params);
                this.viewScript.setText(this.viewScript.getText()
                        + attachScript);
                CreateEvent(dataSetEvents, "add", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_ATTACH_ADD" + "']");
                CreateEvent(dataSetEvents, "load", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_ATTACH_LOAD" + "']");
                CreateEvent(dataSetEvents, "query", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_QUERY" + "']");
                CreateEvent(dataSetEvents, "select", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_SELECT" + "']");
                CreateEvent(dataSetEvents, "unselect", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_UNSELECT" + "']");
                CreateEvent(dataSetEvents, "remove", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_REMOVE" + "']");
                CreateEvent(dataSetEvents, "beforeremove", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_GRID_BEFORE_REMOVE" + "']");
                if (child.getString("page_size") != null) {
                    dataSet.put("page_size", child.getString("page_size"));
                } else {
                    dataSet.put("autopagesize", "true");
                }
                dataSet.put("fetchall", child.getString("fetchall"));
                if (dataSet.getString("bindname") == null) {
                    dataSet.put("bindname", bp_seq + layout_code + "_"
                            + tab_code + "_" + base_table);
                }
                if (dataSet.getString("bindtarget") == null) {
                    dataSet.put(
                            "bindtarget",
                            child.getString("repeat_bp_seq", "") + layout_code
                                    + "_"
                                    + child.getString("parent_table_tab_code")
                                    + '_' + child.getString("parent_table")
                                    + "_ds");
                }
                dataSet.put("processfunction", bp_seq + layout_code + "_"
                        + tab_code + "_CON_ATTACH_PROCESS");
                dataSet.put("selectable", "true");
                component.put("id", bp_seq + layout_code + "_" + tab_code
                        + "_con_contract_grid_id");
                component.put("bindtarget", bp_seq + layout_code + "_"
                        + tab_code + "_" + base_table + "_ds");
                component.put("marginwidth", child.get("margin_width"));
                component.put("width", child.get("width"));
                component.put("height", child.get("height"));
                component.put("marginheight", child.get("margin_height"));
                component.put("navbar", "true");
                if (fieldResultMap != null
                        && fieldResultMap.getChilds() != null) {
                    CompositeMap columns = component.createChild(
                            component.getPrefix(), component.getNamespaceURI(),
                            "columns");
                    editors = component.createChild(component.getPrefix(),
                            component.getNamespaceURI(), "editors");
                    finalComponent = columns;
                    if (tabButtonResult != null) {
                        CompositeMap toolBar = component.createChild(
                                component.getPrefix(),
                                component.getNamespaceURI(), "toolBar");
                        CompositeMap span = toolBar.createChild("span");
                        span.setText(child.getString("tab_desc"));
                        for (CompositeMap tabButtonChild : tabButtonResult) {
                            createGridButton(tabButtonChild, toolBar);
                        }
                    }
                }
            }

            if ("N".equals(tabReloadFlag) && "N".equals(tempTreeFlag)) {
                CreateEvent(dataSetEvents, "update", "window[" + "'" + bp_seq
                        + layout_code + "_" + tab_code
                        + "_ON_LAYOUT_DYNAMIC_INNER_UPDATE" + "']");
            }

            if (fieldResultMap != null) {
                List<CompositeMap> fieldResult = fieldResultMap.getChilds();
                if (fieldResult != null) {
                    CompositeMap fields = null;
                    if ("N".equals(tabReloadFlag)) {
                        fields = dataSet.createChild(dataSet.getPrefix(),
                                dataSet.getNamespaceURI(), "fields");
                    } else {
                        fields = dataSet.getChild("fields");
                    }
                    for (CompositeMap fieldChild : fieldResult) {
                        if (!"DIV".equals(fieldChild.get("validation_type"))) {
                            if (in(fieldChild.get("validation_type"),
                                    new Object[]{"LOV", "TREE", "COMBOBOX",
                                            "LOV_D"})) {
                                createDataSetField(fieldChild, fields, child,
                                        "Y");
                                createDataSetField(fieldChild, fields, child,
                                        "C");
                            } else {
                                createDataSetField(fieldChild, fields, child,
                                        "N");
                            }
                        }
                        if ("Y".equals(fieldChild.getString("display_flag"))) {
                            if (in(tab_type, new Object[]{"GRID",
                                    "SINGLE_GRID", "TABLE", "ATTACH"})) {
                                createGridColumn(fieldChild, finalComponent,
                                        child, editors);
                            } else if ("TEXTAREA".equals(tab_type)) {
                                createFormField(fieldChild, finalComponent,
                                        child, null);
                            }
                        }
                        if ("FIELDBOXCOLUMN".equals(tab_type)
                                && "Y".equals(fieldChild
                                .getString("display_flag"))) {
                            if (fieldChild.get("field_group") != null) {
                                Integer field_group = fieldChild
                                        .getInt("field_group");
                                String fieldGroupId = bp_seq + layout_code
                                        + "_" + child.getString("tab_code", "")
                                        + "_" + field_group;
                                String createFieldGroupFlag = "N";
                                CompositeMap fieldGroup = CompositeUtil
                                        .findChild(finalComponent,
                                                "fieldGroup", "id",
                                                fieldGroupId);
                                if (fieldGroup == null) {
                                    fieldGroup = finalComponent.createChild(
                                            finalComponent.getPrefix(),
                                            finalComponent.getNamespaceURI(),
                                            "fieldGroup");
                                    createFieldGroupFlag = "Y";
                                    fieldGroup.put("id", fieldGroupId);
                                    if (fieldChild.getString("prompt") != null) {
                                        fieldGroup.put("prompt",
                                                fieldChild.getString("prompt"));
                                    }
                                }
                                createFormField(fieldChild, fieldGroup, child,
                                        createFieldGroupFlag);
                            } else {
                                createFormField(fieldChild, finalComponent,
                                        child, null);
                            }
                        }
                    }
                }
            }
            if (in(tab_type, new Object[]{"FORM", "GRIDBOX", "FORMTOOLBAR",
                    "FORMBODY"})) {

                CompositeMap fieldResultMap1 = serverLayoutService.selectLayoutFormConfigField(prepareParam(cptParameters));
//                CompositeMap fieldResultMap1 = queryData(formFieldConfigImport, cptParameters, so1);
                if (fieldResultMap1 != null) {
                    List<CompositeMap> fieldResult1 = fieldResultMap1
                            .getChilds();
                    if (fieldResult1 != null) {
                        for (CompositeMap formFieldChild : fieldResult1) {
                            if (in(tab_type, new Object[]{"FORM",
                                    "FORMTOOLBAR", "FORMBODY"})) {
                                createFormField(formFieldChild, finalComponent,
                                        child, null);
                            } else {
                                createGridColumn(formFieldChild,
                                        finalComponent, child, editors);
                            }
                        }
                    }
                }
            }

            if ("FORMTOOLBAR".equals(tab_type)) {
                createFormBody(child, parent, dataSets);
            }
        }
    }

    private void createBaseTabField(CompositeMap child, String flag)
            throws Exception {
        CompositeMap fieldBinder = CompositeUtil.findChild(
                this.enableTabResultMap, "record", "tab_code",
                child.getString("base_tab_code"));
        if (fieldBinder != null) {
            CompositeMap dataSets = CompositeUtil.findChild(config, "dataSets");
            if (dataSets != null) {
                String baseDsId = bp_seq + layout_code + "_"
                        + fieldBinder.getString("tab_code") + "_"
                        + fieldBinder.getString("base_table") + "_ds";
                CompositeMap dataSet = CompositeUtil.findChild(dataSets,
                        "dataSet", "id", baseDsId);
                if (dataSet != null) {
                    CompositeMap fields = CompositeUtil.findChild(dataSet,
                            "fields");
                    if (fields != null) {
                        createDataSetField(child, fields, fieldBinder, flag);
                    }
                }
            }
        }
    }

    private void createDataSetField(CompositeMap child, CompositeMap fields,
                                    CompositeMap tabConfig, String flag) throws Exception {
        if (child.getString("base_tab_code") != null
                && !child.getString("base_tab_code").equals(
                tabConfig.getString("tab_code"))) {
            createBaseTabField(child, flag);
            return;
        }
        String validation_type = (String) child.get("validation_type");
        CompositeMap field = fields.createChild(fields.getPrefix(),
                fields.getNamespaceURI(), "field");
        if (in(flag, new Object[]{"N", "C"})) {
            CompositeMap fieldOld = CompositeUtil.findChild(fields, "field",
                    "name", child.getString("column_name"));
            if (fieldOld != null) {
                fields.removeChild(fieldOld);
            }
            field.put("name", child.getString("column_name"));
            field.put("defaultvalue", child.getString("default_value"));
        } else if ("Y".equals(flag)) {
            CompositeMap fieldOld = CompositeUtil.findChild(fields, "field",
                    "name", child.getString("column_name") + "_n");
            if (fieldOld != null) {
                fields.removeChild(fieldOld);
            }
            field.put("name", child.getString("column_name") + "_n");
            field.put("defaultvalue", child.getString("default_value_name"));
        }

        if ("Y".equals(child.getString("ignore_required"))
                && "true".equals(child.getString("required_input_mode")
                .toLowerCase())) {
            field.put("requiredfunction", layout_code + "_not_ignore_required");
        }

        field.put("requiredmessage", tabConfig.getString("tab_desc", "")
                + child.getString("prompt") + "${l:HLS.NOT_NULL}");
        if ("QUERY".equals(params.getString("function_usage"))) {
            field.put("readonly", "true");
        } else {
            field.put("readonly", child.getString("readonly_input_mode")
                    .toLowerCase());
        }
        field.put("required", child.getString("required_input_mode")
                .toLowerCase());
        if ("C".equals(flag)) {
            return;
        }
        String validationSql = child.getString("validation_sql");
        if ("CHECKBOX".equals(validation_type)) {
            field.put("checkedvalue", "Y");
            field.put("uncheckedvalue", "N");
        } else if ("BOOLEAN_CHECKBOX".equals(validation_type)) {
            field.put("checkedvalue", "1");
            field.put("uncheckedvalue", "0");
        } else if (in(validation_type, new Object[]{"LOV", "LOV_C", "TREE",
                "TREE_C", "LOV_D", "LOV_D_C"})) {
            field.put("autocomplete", child.get("autocomplete"));
            field.put("fetchsingle", child.get("fetchsingle"));
            field.put("lovgridheight", child.getInt("lovgridheight"));
            field.put("lovheight", child.getInt("lov_height"));
            field.put("lovwidth", child.getInt("lov_width"));
            field.put("lovlabelwidth", "100");
            field.put("title", child.get("prompt"));
            field.put("layout_code", child.get("layout_code"));
            field.put("tab_code", child.get("tab_code"));
            field.put("real_column_name", child.get("column_name"));
            if (in(validation_type, new Object[]{"LOV", "LOV_C"})) {
                if (StringUtils.isNotEmpty(validationSql) && validationSql.startsWith("$")) {
                    field.put("lovservice", validationSql);
                } else {
                    //这里不应该执行旧的lov值列表，已经废弃，应该使用server_parameters_lov_d
//                    field.put("lovservice", "layout.server_parameters_lov");
                }
            } else if (in(validation_type, new Object[]{"LOV_D", "LOV_D_C"})) {
                if (StringUtils.isNotEmpty(validationSql) && validationSql.startsWith("$")) {
                    field.put("lovservice", validationSql);
                } else {
//                    field.put("lovservice", "layout.server_parameters_lov_d");
                    field.put("lovservice", "/server/parameters/lov/query~java.util.Map");
                }
            } else {
                field.put(
                        "lovurl",
                        "${/request/@context_path}/modules/layout/server_layout_lov_tree.lview?tab_code="
                                + tabConfig.getString("tab_code")
                                + "&layout_code="
                                + layout_code
                                + "&column_name=" + child.get("column_name"));
            }
            CompositeMap mapping = field.createChild(field.getPrefix(),
                    field.getNamespaceURI(), "mapping");
            createFieldMapping(mapping, "value_code",
                    child.getString("column_name"));
            if (in(validation_type, new Object[]{"LOV", "LOV_D"})) {
                createFieldMapping(mapping, "value_name",
                        child.getString("column_name") + "_n");
            }
            if ("LOV_D".equals(validation_type)) {
                Map<String, Object> lovConfigQueryParameters = new HashMap<>();
                lovConfigQueryParameters.put("layout_code", layout_code);
                lovConfigQueryParameters.put("config_id", child.getString("config_id"));

                CompositeMap lovConfigResultMap = serverLayoutService.selectLayoutConfigLov(lovConfigQueryParameters);
//                CompositeMap lovConfigResultMap = queryData(lovConfigImport, lovConfigQueryParameters);
                if (lovConfigResultMap != null) {
                    List<CompositeMap> lovConfigResult = lovConfigResultMap.getChilds();
                    if (lovConfigResult != null) {
                        for (CompositeMap lovChild : lovConfigResult) {
                            if (!in(lovChild.getString("lov_col_name")
                                    .toUpperCase(), new Object[]{
                                    "VALUE_CODE", "VALUE_NAME"})) {
                                createFieldMapping(mapping,
                                        lovChild.getString("lov_col_name")
                                                .toLowerCase(), lovChild
                                                .getString("lov_col_name")
                                                .toLowerCase());
                            }
                        }
                    }
                }
            }
        } else if (in(validation_type, new Object[]{"COMBOBOX", "COMBOBOX_C"})) {
            if (!"C".equals(flag)) {
                CompositeMap comboBoxDataSet = new CompositeMap(
                        fields.getPrefix(), fields.getNamespaceURI(), "dataSet");
                comboBoxDataSet.put(
                        "id",
                        bp_seq + layout_code + "_"
                                + tabConfig.getString("tab_code") + "_"
                                + child.getString("column_name")
                                + "_combobox_ds");
                comboBoxDataSet.put("fetchall", "true");

                if (StringUtils.isNotEmpty(validationSql) && validationSql.startsWith("$")) {
                    comboBoxDataSet.put("queryurl", "${/request/@context_path}/doc/layout/comboBox/common");
                } else {
                    comboBoxDataSet.put("queryurl", "${/request/@context_path}/server/parameters/lov/comboBox/query");
                }

                List dataSetsLists = fields.getParent().getParent().getChilds();
                int index = dataSetsLists.indexOf(fields.getParent());
                dataSetsLists.add(index, comboBoxDataSet);
            }
            field.put("returnfield", child.get("column_name"));
            field.put("valuefield", "value_code");
            field.put("displayfield", "value_name");
            field.put(
                    "options",
                    bp_seq + layout_code + "_"
                            + tabConfig.getString("tab_code") + "_"
                            + child.getString("column_name") + "_combobox_ds");
        } else if ("DATEPICKER".equals(validation_type)) {
            field.put("datatype", "java.util.Date");
        }
    }

    private String getValidationType(String validation_type) {
        if (validation_type == null) {
            return null;
        }
        String temp_validation_type = null;
        if ("TEXTFIELD".equals(validation_type)) {
            temp_validation_type = "textField";
        } else if ("NUMBERFIELD".equals(validation_type)) {
            temp_validation_type = "numberField";
        } else if ("DATEPICKER".equals(validation_type)) {
            temp_validation_type = "datePicker";
        } else if ("PERCENTFIELD".equals(validation_type)) {
            temp_validation_type = "percentField";
        } else if ("TEXTAREA".equals(validation_type)) {
            temp_validation_type = "textArea";
        } else if (in(validation_type, new Object[]{"CHECKBOX",
                "BOOLEAN_CHECKBOX"})) {
            temp_validation_type = "checkBox";
        } else if ("COMBOBOX".equals(validation_type)) {
            temp_validation_type = "comboBox";
        } else if (in(validation_type, new Object[]{"TREE", "TREE_C",
                "LOV_D", "LOV_D_C", "LOV", "LOV_C"})) {
            temp_validation_type = "lov";
        } else if (in(validation_type, new Object[]{"LINK", "BUTTON"})) {
            temp_validation_type = "label";
        } else if ("FIELDGROUP".equals(validation_type)) {
            temp_validation_type = "fieldGroup";
        } else {
            temp_validation_type = validation_type.toLowerCase();
        }
        return temp_validation_type;
    }

    private void createFormField(CompositeMap child, CompositeMap component,
                                 CompositeMap tabConfig, String createFieldGroupFlag) {
        String validation_type = child.getString("validation_type");
        String temp_validation_type = getValidationType(validation_type);
        if ("N".equals(createFieldGroupFlag)
                && child.getString("field_group") != null) {
            CompositeMap span = component.createChild("div");
            span.put("style", "white-space:nowrap;color:#888;font-size:12px;");
            span.setText(child.getString("prompt"));
        }
        CompositeMap field = null;
        if (params.getString("tree_code") != null
                && "TEXTAREA".equals(tabConfig.getString("tab_type"))) {
            CompositeMap div1 = component.createChild("div");
            CompositeMap div2 = div1.createChild("div");
            div2.setText(child.getString("prompt"));
            field = div1.createChild(component.getPrefix(),
                    component.getNamespaceURI(), temp_validation_type);
        } else {
            if ("div".equals(temp_validation_type)) {
                field = component.createChild(temp_validation_type);
            } else {
                field = component.createChild(component.getPrefix(),
                        component.getNamespaceURI(), temp_validation_type);
                field.put("colspan", child.get("colspan"));
                field.put("rowspan", child.get("rowspan"));
            }
        }
        String base_table = tabConfig.getString("base_table", "");
        if (params.getString("tree_code") == null
                || !"TEXTAREA".equals(tabConfig.getString("tab_type"))) {
            field.put("prompt", child.getString("prompt"));
        }
        if (!"div".equals(temp_validation_type)) {
            String fieldId = null;
            String fieldBindTarget = null;
            if (child.getString("base_tab_code") != null
                    && !child.getString("base_tab_code").equals(
                    tabConfig.getString("tab_code"))) {
                CompositeMap fieldBinder = CompositeUtil.findChild(
                        this.enableTabResultMap, "record", "tab_code",
                        child.getString("base_tab_code"));
                if (fieldBinder != null) {
                    fieldId = (bp_seq + layout_code + "_"
                            + tabConfig.getString("tab_code") + "_"
                            + tabConfig.getString("base_table") + "_" + child
                            .getString("column_name")).toUpperCase();
                    fieldBindTarget = bp_seq + layout_code + "_"
                            + fieldBinder.getString("form_binder_tab_code")
                            + "_" + fieldBinder.getString("base_table") + "_ds";
                } else {
                    return;
                }
            } else {
                fieldId = (bp_seq + layout_code + "_"
                        + tabConfig.getString("tab_code") + "_"
                        + tabConfig.getString("base_table") + "_" + child
                        .getString("column_name")).toUpperCase();
                fieldBindTarget = bp_seq + layout_code + "_"
                        + tabConfig.getString("form_binder_tab_code") + "_"
                        + base_table + "_ds";
            }
            field.put("id", fieldId);
            field.put("bindtarget", fieldBindTarget);
        }
        if ("TEXTAREA".equals(tabConfig.getString("tab_type"))) {
            field.put("width", tabConfig.get("width"));
            field.put("marginheight", tabConfig.get("margin_height"));
            field.put("marginwidth", tabConfig.get("margin_width"));
            field.put("height", tabConfig.get("height"));
        } else {
            field.put("width", child.get("width"));
            if ("TEXTAREA".equals(validation_type)) {
                field.put("height", child.get("height"));
            }
        }
        if ("Y".equals(child.getString("underline"))) {
            field.put("classname", "item-tf-baseline");
        }

        if ("Y".equals(child.getString("renderer_flag"))) {
            field.put(
                    "renderer",
                    bp_seq + layout_code + "_"
                            + tabConfig.getString("tab_code")
                            + "_FIELD_LINK_RENDERER");
        }

        String style = "margin-top:" + tabConfig.get("margin_top")
                + "px;margin-bottom:" + tabConfig.get("margin_top") + "px";
        field.put("style", style);
        if (in(validation_type, new Object[]{"NUMBERFIELD", "PERCENTFIELD"})) {
            field.put("name", child.get("column_name"));
            field.put("allowdecimals", child.get("allow_decimal"));
            field.put("allowformat", child.get("allow_format"));
            field.put("allowpad", child.get("zero_fill"));
            field.put("decimalprecision", child.get("precision_num"));
        } else if (in(validation_type, new Object[]{"LOV_C", "LOV", "TREE_C",
                "TREE", "COMBOBOX_C", "COMBOBOX", "LOV_D_C", "LOV_D"})) {
            if (in(validation_type, new Object[]{"LOV_C", "TREE_C",
                    "COMBOBOX_C", "LOV_D_C"}))
                field.put("name", child.get("column_name"));
            else {
                field.put("name", child.get("column_name") + "_n");
            }
            CompositeMap lovEvents = field.createChild(field.getPrefix(),
                    field.getNamespaceURI(), "events");
            CreateEvent(lovEvents, "focus", "window[" + "'" + bp_seq
                    + layout_code + '_' + child.getString("tab_code")
                    + "_ON_FORM_OBJECT_FOCUS" + "']");
            if (in(validation_type, new Object[]{"LOV_C", "TREE_C", "LOV",
                    "TREE", "LOV_D", "LOV_D_C"}))
                CreateEvent(lovEvents, "beforecommit",
                        "ON_COMMON_OBJECT_BEFORECOMMIT");
        } else if (in(validation_type, new Object[]{"TEXTFIELD", "TEXTAREA",
                "DATEPICKER", "CHECKBOX", "BOOLEAN_CHECKBOX"})) {
            field.put("name", child.get("column_name"));
        } else if (in(validation_type, new Object[]{"LINK", "BUTTON"})) {
            field.put("prompt", "");
            field.put("name", child.get("column_name"));
            field.put(
                    "renderer",
                    bp_seq + layout_code + "_"
                            + tabConfig.getString("tab_code")
                            + "_FIELD_LINK_RENDERER");
        }
    }

    private void createGridColumn(CompositeMap child, CompositeMap component,
                                  CompositeMap tabConfig, CompositeMap editors) {
        CompositeMap column = null;
        CompositeMap parentColumn = null;
        if (child.getString("column_group") != null) {
            String column_group_id = bp_seq + layout_code + "_"
                    + tabConfig.getString("tab_code") + "_"
                    + child.getString("column_group") + "_column_group_id";
            if ("Y".equals(child.getString("column_group_flag"))) {
                parentColumn = component.createChild(component.getPrefix(),
                        component.getNamespaceURI(), "column");
                parentColumn.put("prompt",
                        child.getString("column_group_name", null));
                parentColumn.put("id", column_group_id);
                column = parentColumn.createChild(component.getPrefix(),
                        component.getNamespaceURI(), "column");
            } else {
                parentColumn = CompositeUtil.findChild(component, "column",
                        "id", column_group_id);
                column = parentColumn.createChild(component.getPrefix(),
                        component.getNamespaceURI(), "column");
            }

        } else {
            column = component.createChild(component.getPrefix(),
                    component.getNamespaceURI(), "column");
        }
        String validation_type = (String) child.get("validation_type");
        String temp_validation_type = getValidationType(validation_type);
        if ("div".equals(temp_validation_type)) {
            return;
        }
        if (child.getInt("display_order", 0) < 0) {
            column.put("lock", "true");
        }
        if ("GRIDBOX".equals(tabConfig.getString("tab_type"))) {
            column.put("labelwidth", tabConfig.get("label_width"));
        }
        if ("Y".equals(child.getString("sortable"))) {
            column.put("sortable", "true");
        }
        column.put("align", child.getString("alignment"));
        column.put("prompt", child.getString("prompt"));
        column.put("width", child.getString("width"));
        if ("label".equals(temp_validation_type.toLowerCase())) {
            temp_validation_type = "textField";
        } else {
            column.put("editorfunction",
                    bp_seq + layout_code + "_" + child.getString("tab_code")
                            + "_SEE_DETAIL_EDITOR");
        }
        if (in(validation_type, "LOV", "TREE", "COMBOBOX", "LOV_D"))
            column.put("name", child.getString("column_name") + "_n");
        else {
            column.put("name", child.getString("column_name"));
        }
        if ("GRIDBOX".equals(tabConfig.getString("tab_type"))
                && child.getString("colspan") != null) {
            column.put("colspan", child.getString("colspan"));
        }
        CompositeMap editor = editors.createChild(editors.getPrefix(),
                editors.getNamespaceURI(), temp_validation_type);
        editor.put("id",
                bp_seq + layout_code + "_" + child.getString("tab_code") + "_"
                        + child.getString("column_name") + "_id");
        if ("Y".equals(child.getString("renderer_flag"))) {
            column.put("renderer",
                    bp_seq + layout_code + "_" + child.getString("tab_code")
                            + "_SEEDETAIL_COLUMN_LN");
        }
        if ("TEXTFIELD".equals(validation_type)) {
            column.put("showtitle", child.getString("sys_grid_show_title"));
            column.put("footerrenderer", child.getString("footerrenderer"));
        } else if ("NUMBERFIELD".equals(validation_type)) {
            column.put("footerrenderer", child.getString("footerrenderer"));
            column.put("renderer",
                    bp_seq + layout_code + "_" + child.getString("tab_code")
                            + "_SEEDETAIL_COLUMN_LN");
            editor.put("decimalprecision", "-1");
            if ("FALSE".equals(child.getString("allow_decimal"))) {
                editor.put("allowdecimals", "false");
            }
        } else if ("DATEPICKER".equals(validation_type)) {
            column.put("renderer", "Leaf.formatDate");
        } else if ("PERCENTFIELD".equals(validation_type)) {
            column.put("renderer",
                    bp_seq + layout_code + "_" + child.getString("tab_code")
                            + "_SEEDETAIL_COLUMN_LN");
            editor.put("decimalprecision", "-1");
            if ("FALSE".equals(child.getString("allow_decimal"))) {
                editor.put("allowdecimals", "false");
            }
        } else if ("TEXTAREA".equals(validation_type)) {
            column.put("showtitle", child.getString("sys_grid_show_title"));
            if (child.getString("height") != null) {
                editor.put("height", child.getString("height"));
            }
        } else if ("PICTURE".equals(validation_type)) {
            column.put("renderer",
                    bp_seq + layout_code + "_" + child.getString("tab_code")
                            + "_SEEDETAIL_LN_PICTURE");
            column.put("showtitle", child.getString("sys_grid_show_title"));
        } else if (in(validation_type, new Object[]{"LINK", "BUTTON"})) {
            column.put("renderer",
                    bp_seq + layout_code + "_" + child.getString("tab_code")
                            + "_RENDERER");
        } else if (in(validation_type, new Object[]{"LOV_C", "LOV", "TREE_C",
                "TREE", "COMBOBOX_C", "COMBOBOX", "LOV_D", "LOV_D_C"})) {
            CompositeMap lovEvents = editor.createChild(editor.getPrefix(),
                    editor.getNamespaceURI(), "events");
            if ("ATTACH".equals(tabConfig.getString("tab_type").toUpperCase())) {
                CreateEvent(lovEvents, "focus", "window[" + "'" + bp_seq
                        + layout_code + '_' + child.getString("tab_code")
                        + "_ON_ATTACH_OBJECT_FOCUS" + "']");
            } else {
                CreateEvent(lovEvents, "focus", "window[" + "'" + bp_seq
                        + layout_code + '_' + child.getString("tab_code")
                        + "_ON_GRID_OBJECT_FOCUS" + "']");
            }
            if (in(validation_type, new Object[]{"LOV_C", "TREE_C", "LOV",
                    "TREE", "LOV_D", "LOV_D_C"}))
                CreateEvent(lovEvents, "beforecommit",
                        "ON_COMMON_OBJECT_BEFORECOMMIT");
        }
    }

    private void createGridButton(CompositeMap childConfig, CompositeMap parent) {
        CompositeMap button = parent.createChild(parent.getPrefix(),
                parent.getNamespaceURI(), "button");
        String button_code = childConfig.getString("button_code");
        button.put("id",
                bp_seq + layout_code + "_" + childConfig.getString("tab_code")
                        + "_" + button_code + "_layout_dynamic_button_id");
        button.put("text", childConfig.getString("prompt"));
        if ("QUERY".equals(button_code)) {
            button.put(
                    "click",
                    bp_seq + layout_code + '_'
                            + childConfig.getString("tab_code") + "_GRID_QUERY");
            button.put("type", "query");
        } else if (in(button_code, new Object[]{"ADD", "DELETE", "SAVE",
                "CLEAR", "EXCEL"})) {
            button.put("type", button_code.toLowerCase());
        } else {
            String clickText1 = "${/parameter/@layout_code}_"
                    + childConfig.getString("tab_code") + "_" + button_code
                    + "_layout_dynamic_tab_click";
            String clickText2 = bp_seq + layout_code + "_"
                    + childConfig.getString("tab_code") + "_" + button_code
                    + "_layout_dynamic_tab_click";
            String clickText3 = bp_seq + "${/parameter/@layout_code}_"
                    + childConfig.getString("tab_code") + "_" + button_code
                    + "_layout_dynamic_tab_click";
            if (this.viewScript.getText().indexOf(clickText1) != -1
                    || this.viewScript.getText().indexOf(clickText2) != -1
                    || this.viewScript.getText().indexOf(clickText3) != -1) {
                button.put("click", clickText1);
                if (childConfig.getString("icon") != null) {
                    button.put("icon",
                            "${/request/@context_path}/images/layout_button/"
                                    + childConfig.getString("icon"));
                }
            }
        }
    }

    private String str(String... strings) {
        if (strings == null || strings.length < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string);
        }
        return sb.toString();
    }

    private boolean in(Object target, Object... source) {
        if (target == null || source == null || source.length < 1) {
            return false;
        }
        for (Object o : source) {
            if (target.equals(o)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> prepareParam(Map<String, Object> map) {
        HashMap<String, Object> param = new HashMap<>(map);
        CompositeMap session = context.getChild("session");
        IRequest currentRequest = RequestHelper.getCurrentRequest(true);
        param.put("user_id", session.getString("user_id"));
        param.put("role_id", session.getString("role_id"));
        param.put("company_id", session.getString("company_id"));
        param.put("lov_search_rule", session.getString("lov_search_rule"));
        return param;
    }
}