package leaf.presentation.component.std;


import leaf.application.LeafApplication;
import leaf.presentation.BuildSession;
import leaf.presentation.ViewContext;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lrd
 * Date: 2018/5/4
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */

public class LeftNavBars extends GridLayout {

    public static final String PROPERTITY_NAVIGATIONBARTITLE = "navigationBarTitle";
    public static final String PROPERTITY_NAVIGATIONBAR = "navigationBar";
    public static final String PROPERTITY_NAVIGATIONBARCONTENT = "navigationBarContent";
    public static final String PROPERTITY_TREETITLE = "treeTitle";
    public static final String PROPERTITY_NAVIGATIONBARID = "navigationbarId";
    public static final String PROPERTITY_NAVIGATIONBARCLASS = "navigationbarClass";

    public LeftNavBars(IObjectRegistry registry) {
        super(registry);
    }


    public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
        super.onPreparePageContent(session, context);
        addStyleSheet(session, context, "navigationBar/navigationBar.css");
        addJavaScript(session, context, "navigationBar/navigationBar.js");
    }

    public Map getLeftTreeInfo(String type,List<CompositeMap> childs,BuildSession session,CompositeMap view,CompositeMap model){
        Map map = new HashMap();
        StringBuilder rightInfo = new StringBuilder();
        StringBuffer sb = new StringBuffer();
        if("normal".equals(type)){
            if( childs != null){
                List<Map> node =  new ArrayList<>();
                for(int i = 0; i < childs.size(); i++){
                    CompositeMap compositeMap = childs.get(i);
                    if(compositeMap.get("ref") != null){
                        CompositeMap screenInclude = new CompositeMap("a", LeafApplication.LEAF_FRAMEWORK_NAMESPACE, "screen-include");
                        screenInclude.put("screen",compositeMap.get("ref"));
                        compositeMap.addChild(screenInclude);
                    }
                    node.add(compositeMap);

                }
                rightInfo.append(getContent(session,node.iterator(),view,model));
            }

            sb.append(type);
            sb.append(",");
            if(childs!=null){
                for(int i = 0; i < childs.size(); i++){
                    if(childs.get(i).get("title") != null){
                        sb.append(childs.get(i).get("title"));
                        sb.append(",");
                    }
                    if(childs.get(i).get("id") != null){
                        sb.append(childs.get(i).get("id"));
                        sb.append(",");
                    }

                }
            }
        }else if("tree".equals(type)){
            List<Map> node = null;
            for(int i = 0; i < childs.size(); i++){
                if(childs.get(i).get("parentid") != null && childs.get(i).get("id") != null && childs.get(i).get("parentid").toString().equals(childs.get(i).get("id").toString())){
                    String parentId = childs.get(i).get("parentid").toString();
                    sb.append(type);
                    sb.append(",");
                    sb.append(childs.get(i).get("grouptitle"));
                    sb.append(",");
                    sb.append(childs.get(i).get("id"));
                    sb.append(",");
                    sb.append(childs.get(i).get("title"));
                    sb.append(",");
                    sb.append(childs.get(i).get("id"));
                    sb.append(",");
                    node = new ArrayList<>();
                    CompositeMap compositeMap = childs.get(i);
                    if(compositeMap.get("ref") != null){
                        CompositeMap screenInclude = new CompositeMap("a", LeafApplication.LEAF_FRAMEWORK_NAMESPACE, "screen-include");
                        screenInclude.put("screen",compositeMap.get("ref"));
                        compositeMap.addChild(screenInclude);
                    }
                    node.add(compositeMap);
                    rightInfo.append(getContent(session,node.iterator(),view,model));
                    for(int j = 0; j < childs.size(); j++){
                        if(j != i && parentId.equals(childs.get(j).get("parentid"))&&!childs.get(j).get("id").equals(parentId)){
                            sb.append(childs.get(j).get("title"));
                            sb.append(",");
                            sb.append(childs.get(j).get("id"));
                            sb.append(",");
                            node = new ArrayList<>();
                            node.add(childs.get(j));
                            rightInfo.append(getContent(session,node.iterator(),view,model));
                        }
                    }
                }

            }
            sb.append("tree,");
        }
        /*map.put("navigationBarTitle",treeList);*/
        map.put("leftContent",sb.toString().substring(0,sb.toString().length()-1));
        map.put("rightContent",rightInfo.toString());
        return map;
    }

    public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
        super.onCreateViewContent(session, context);
        CompositeMap view = context.getView();

        Map map = context.getMap();

        String id = "";
        if(view.get("id") != null){
            id = view.get("id").toString();
        }else{
            id = "left_"+System.currentTimeMillis()+(int)(Math.random()*100)+"_bar";
        }

        Map info = getLeftTreeInfo(view.get("type").toString(),view.getChilds(),session,view,context.getModel());
        addConfig("navigationBarTitle",info.get("leftContent"));
        map.put("content",info.get("rightContent"));
        map.put("id",id);
        if(session.getScreenTopToolbar()){
            map.put("top","90px");
        }else{
            map.put("top","20px");
        }
        map.put(CONFIG,getConfig());
    }

    public String getContent(BuildSession session,Iterator it,CompositeMap view,CompositeMap model){

        StringBuffer sb = new StringBuffer();
        try {
            sb.append(buildRows(session, model, view, it));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  sb.toString();
    }

    public String content(BuildSession session,ViewContext view_context){
        CompositeMap view = view_context.getView();
        CompositeMap model = view_context.getModel();
        Map map = view_context.getMap();
        Iterator it = view.getChildIterator();
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(buildRows(session, model, view, it));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  sb.toString();
    }

    /*public void buildView(BuildSession session, ViewContext view_context) throws IOException, ViewCreationException {
        CompositeMap view = view_context.getView();
        CompositeMap model = view_context.getModel();
        Map map = view_context.getMap();
        Iterator it = view.getChildIterator();
        Writer out = session.getWriter();
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(buildRows(session, model, view, it));
        } catch (Exception e) {
            e.printStackTrace();
        }
        *//*sb.append("<div style=\"width:100px;height:100px;background:red;\"></div>");*//*
        out.write(sb.toString());
    }

    @Override
    public String[] getBuildSteps(ViewContext context) {
        return null;
    }*/

    /*public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException {
        super.onCreateViewContent(session, context);
        CompositeMap view = context.getView();

        Map map = context.getMap();

        LeftNavBarConfig nc = LeftNavBarConfig.getInstance();
        addConfig(LeftNavBarConfig.PROPERTITY_NAVIGATIONBARTITLE,nc.getNavigationBarTitle(view));

        map.put(nc.PROPERTITY_NAVIGATIONBARID,nc.getNavigationBarId());
        map.put(nc.PROPERTITY_NAVIGATIONBARCLASS,nc.getNavigationBarClass());

    }*/
    /*public static final String PROPERTITY_NAVIGATIONBARTITLE = "navigationBarTitle";
    public static final String PROPERTITY_NAVIGATIONBAR = "navigationBar";
    public static final String PROPERTITY_NAVIGATIONBARCONTENT = "navigationBarContent";
    public static final String PROPERTITY_TREETITLE = "treeTitle";
    public static final String PROPERTITY_NAVIGATIONBARID = "navigationbarId";
    public static final String PROPERTITY_NAVIGATIONBARCLASS = "navigationbarClass";

    public static HlsNavigationBar createInstance() {
        XMap view = new XMap(DEFAULT_TAG_PREFIX, DEFAULT_NAME_SPACE, PROPERTITY_NAVIGATIONBAR);
        HlsNavigationBar HlsNavigationBar = new HlsNavigationBar();
        HlsNavigationBar.initPrototype(view);
        return HlsNavigationBar;
    }

    public String getNavigationBarContent(ViewContext context) throws Exception {
        StringBuffer sb = new StringBuffer();
        List<XMap> list = getPrototype().getChildren();
        for (XMap map : list) {
            sb.append(ScreenBuilder.build(map, context));
        }
        return sb.toString();
    }

    public List<String> getNavigationBarTitle(ViewContext context) throws Exception {
        List<String> titleList = new ArrayList<>();
        List<XMap> list = getPrototype().getChildren();
        for (XMap map : list){
            if(map.get("navigationBar")!=null){
                if(this.getPrototype().getString("barType")!=null){
                    if(this.getPrototype().getString("barType").equals("normal")){
                        titleList.add("normal");
                        break;
                    }
                }
            }
        }
        int currentIndex = 0;
        for (XMap map : list){
            if(map.get("navigationBar")!=null){
                if(this.getPrototype().getString("barType").equals("tree")){
                    currentIndex++;
                    if(map.get("nodeLevel")!=null){
                        if( map.get("nodeLevel").toString().equals("parent")){
                            titleList.add("tree");
                            if(map.get("treeTitle").toString()!=null){
                                titleList.add(map.get("treeTitle").toString());
                            }else {
                                titleList.add(currentIndex+". 第"+currentIndex+"模块");
                            }
                            getbarId(map,titleList,currentIndex);
                        }
                    }
                    getBarData(map,titleList,currentIndex);
                }else if(this.getPrototype().getString("barType").equals("normal")){
                    currentIndex++;
                    getBarData(map,titleList,currentIndex);
                }
            }
        }
        return titleList;
    }

    public void getBarData(XMap map, List<String> titleList, int currentIndex){
        if(map.get("navigationBarTitle")!=null){
            titleList.add(map.get("navigationBarTitle").toString());
            getbarId(map,titleList,currentIndex);
        }else if(map.get("title")!=null && map.get("navigationBarTitle")==null){
            titleList.add(map.get("title").toString());
            getbarId(map,titleList,currentIndex);
        }else if(map.get("title")==null && map.get("navigationBarTitle")==null){
            titleList.add(currentIndex+". 第"+currentIndex+"模块");
            getbarId(map,titleList,currentIndex);
        }
    }

    public void getbarId(XMap map, List<String> titleList, int currentIndex){
        if(map.get("id")!=null){
            titleList.add(map.get("id").toString());
        }else {
            titleList.add("section-bar-"+currentIndex);
        }
    }

    public String getNavigationbarId(ViewContext context) throws Exception {
        String str = "";
        if(this.getPrototype().getString("id")!=null){
            str = this.getPrototype().getString("id");
        }
        return str.toString();
    }

    public String getNavigationbarClass(ViewContext context) throws Exception {
        String str = "";
        if(this.getPrototype().getString("class")!=null){
            str = this.getPrototype().getString("class");
        }
        return str.toString();
    }

    public void init(XMap view, ViewContext context) throws Exception {
        super.init(view, context);
        context.put(PROPERTITY_NAVIGATIONBARCONTENT, getNavigationBarContent(context));
        context.addJsonConfig(PROPERTITY_NAVIGATIONBARTITLE, getNavigationBarTitle(context));
        context.put(PROPERTITY_NAVIGATIONBARCLASS, getNavigationbarClass(context));
        context.put(PROPERTITY_NAVIGATIONBARID, getNavigationbarId(context));
    }*/
}
