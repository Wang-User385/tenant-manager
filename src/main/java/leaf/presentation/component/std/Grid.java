package leaf.presentation.component.std;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import leaf.utils.ConfigUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.composite.TextParser;
import uncertain.ocm.IObjectRegistry;
import leaf.application.ApplicationViewConfig;
import leaf.application.LeafApplication;
import leaf.presentation.BuildSession;
import leaf.presentation.ViewContext;
import leaf.presentation.component.std.config.ButtonConfig;
import leaf.presentation.component.std.config.ComponentConfig;
import leaf.presentation.component.std.config.DataSetConfig;
import leaf.presentation.component.std.config.GridColumnConfig;
import leaf.presentation.component.std.config.GridConfig;
import leaf.presentation.component.std.config.NavBarConfig;
import leaf.presentation.component.std.config.TextFieldConfig;

/**
 *
 * @version $Id: Grid.java,v 1.1 2016/09/02 08:21:30 wuhuazhen2689 Exp $
 * @author <a href="mailto:znjqolf@126.com">vincent</a>
 */
@SuppressWarnings("unchecked")
public class Grid extends Component {

    public static final String VERSION = "$Revision: 1.1 $";

    public static final String HTML_LOCKAREA = "lockarea";
    public static final String HTML_UNLOCKAREA = "unlockarea";

    private static final String DEFAULT_CLASS = "item-grid-wrap";
    private static final String MAX_ROWS = "maxRow";
    private static final String ROW_SPAN = "rowspan";
    private static final String COL_SPAN = "colspan";
    private static final String ROW_HEIGHT = "rowHeight";
    private static final String HEAD_HEIGHT = "headHeight";
    private static final String LOCK_WIDTH = "lockwidth";
    private static final String UNLOCK_WIDTH = "unlockwidth";
    private static final String BODY_HEIGHT = "bodyHeight";
    private static final String ROW_WIDTH = "rowWidth";
    private static final String TABLE_HEIGHT = "tableHeight";
    private static final String FOOTER_BAR = "footerBar";
    private static final String LOCK_COLUMNS = "lockcolumns";
    private static final String UNLOCK_COLUMNS = "unlockcolumns";
    private static final String UNLOCK_FILTER = "unlockfilter";

    private static final String COLUMN_TYPE = "type";
    private static final String TYPE_CELL_CHECKBOX = "cellcheck";
    //	private static final String TYPE_CELL_RADIO = "cellradio";
    private static final String TYPE_ROW_CHECKBOX = "rowcheck";
    private static final String TYPE_ROW_RADIO = "rowradio";
    private static final String TYPE_ROW_NUMBER = "rownumber";

    private static final String COMPONENT_BUTTON = "button";
    private static final String TYPE_EXCEL = "excel";

    private static final String LEAF_EXCEL_BUTTON = "leaf.gridExcelButton";

    private int rowHeight = 25;
    private int headHeight = 25;
    private int navbarHeight = 25;
    private boolean hasFooterBar = false;

    public Grid(IObjectRegistry registry) {
        super(registry);
    }

    public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
        super.onPreparePageContent(session, context);
        addStyleSheet(session, context, "grid/Grid-min.css");
        addJavaScript(session, context, "grid/Grid-min.js");
    }

    protected int getDefaultWidth(BuildSession session) {
        return 800;
    }

    protected String getDefaultClass(BuildSession session, ViewContext context){
        CompositeMap view = context.getView();
        CompositeMap model = context.getModel();
        GridConfig gcc = GridConfig.getInstance(view);
        String cls = DEFAULT_CLASS;
        if(gcc.isAutoShowFilter()){
            cls += " grid-filtering";
        }
        if(!gcc.hasEditorBorder(model,mDefaultEditorBorder)||THEME_BESTSELLER.equals(session.getTheme())||THEME_HLS_DEFAULT.equals(session.getTheme())){
            addConfig(GridConfig.PROPERTITY_EDITOR_BORDER, Boolean.FALSE);
            return cls + " none-editor-border";
        }
        return cls;
    }
    private int mDefaultMarginSize = ApplicationViewConfig.DEFAULT_MARGIN_WIDTH;
    private boolean mDefaultAutoAppend = ApplicationViewConfig.DEFAULT_AUTO_APPEND;
    private boolean mDefaultGridSubmask = ApplicationViewConfig.DEFAULT_GRID_SUBMASK;
    private boolean mDefaultEditorBorder = ApplicationViewConfig.DEFAULT_EDITOR_BORDER;
    private boolean mDefaultAutoCount = ApplicationViewConfig.DEFAULT_AUTO_COUNT;
    private boolean mDefaultAutoAdjustGrid = ApplicationViewConfig.DEFAULT_AUTO_ADJUST_GRID;
    private boolean mDefaultAutoAdjustGridColumn = ApplicationViewConfig.DEFAULT_AUTO_ADJUST_GRID_COLUMN;
    private boolean mDefaultInfiniteLoad = ApplicationViewConfig.DEFAULT_INFINITE_LOAD;

    private boolean isBestseller = false;
    private boolean isHlsDefault = false;
    private boolean isHap = false;

    private boolean hasFilter = false;
    private boolean isAutoShowFilter = true;

    public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
        CompositeMap view = context.getView();

        if(null!=mApplicationConfig){
            ApplicationViewConfig view_config = mApplicationConfig.getApplicationViewConfig();
            if (null != view_config) {
                mDefaultMarginSize = view_config.getDefaultMarginWidth();
                mDefaultAutoAppend = view_config.getDefaultAutoAppend();
                mDefaultGridSubmask = view_config.getDefaultGridSubmask();
                mDefaultEditorBorder = view_config.getDefaultEditorBorder();
                mDefaultAutoCount = view_config.getDefaultAutoCount();
                mDefaultAutoAdjustGrid = view_config.getDefaultAutoAdjustGrid();
                mDefaultAutoAdjustGridColumn = view_config.getDefaultAutoAdjustGridColumn();
                mDefaultInfiniteLoad = view_config.getDefaultInfiniteLoad();
            }
        }

        if(mDefaultMarginSize != -1){
            view.putInt(ComponentConfig.PROPERTITY_MARGIN_WIDTH, mDefaultMarginSize);
        }


        CompositeMap model = context.getModel();
        super.onCreateViewContent(session, context);
        GridConfig gc = GridConfig.getInstance(view);
        Map map = context.getMap();
        String theme = session.getTheme();
        isBestseller = THEME_BESTSELLER.equals(theme);
        isHlsDefault =  THEME_HLS_DEFAULT.equals(theme);
        isHap = theme.indexOf(THEME_HAP)==-1?false:true;
        if(isHap){
            rowHeight=35;
            headHeight=32;
            navbarHeight=39;
        }else {
            rowHeight = gc.getRowHeight(isBestseller ? 40 : isHlsDefault ? 33 : 25);
            headHeight = isBestseller ? 50 : isHlsDefault ? 38 : 25;
            navbarHeight = isBestseller ? 50 : isHlsDefault ? 36 : 25;
        }
        boolean hasToolBar = creatToolBar(session,context);
        hasFooterBar = hasFooterBar(gc.getColumns(),model);
        boolean hasNavBar = createNavgationToolBar(session,context);
        isAutoShowFilter = gc.isAutoShowFilter();

        String style = "";
        if(hasToolBar){
            style += "border-top:none;";
        }
        if(hasNavBar||hasFooterBar){
            style += "border-bottom:none;";
        }
        Integer height = (Integer)map.get(ComponentConfig.PROPERTITY_HEIGHT);
        int sh = 0;
        if(hasToolBar) sh +=25;
        if(hasFooterBar) sh +=26;
        if(hasNavBar) sh +=navbarHeight;
        map.put(TABLE_HEIGHT, new Integer(height.intValue()-sh)<rowHeight*2?rowHeight*2:new Integer(height.intValue()-sh));
        String rowRenderer = gc.getRowRenderer();
        if(rowRenderer!=null) addConfig(GridConfig.PROPERTITY_ROW_RENDERER, rowRenderer);
        if(!gc.isAutoFocus()) addConfig(GridConfig.PROPERTITY_AUTO_FOCUS, Boolean.valueOf(gc.isAutoFocus()));
        addConfig(GridConfig.PROPERTITY_AUTO_APPEND, Boolean.valueOf(gc.isAutoAppend(mDefaultAutoAppend)));
        addConfig(GridConfig.PROPERTITY_SUBMASK, gc.getSubMask(mDefaultGridSubmask));
        addConfig(GridConfig.PROPERTITY_CAN_PASTE, Boolean.valueOf(gc.isCanPaste()));
        addConfig(GridConfig.PROPERTITY_CAN_WHEEL, Boolean.valueOf(gc.isCanWheel()));
        addConfig(GridConfig.PROPERTITY_GROUP_SELECT, Boolean.valueOf(gc.isGroupSelect()));
        addConfig(GridConfig.PROPERTITY_ROW_HEIGHT, Integer.valueOf(rowHeight));
        addConfig(GridConfig.PROPERTITY_AUTO_SHOW_FILTER, Boolean.valueOf(isAutoShowFilter));
        addConfig("headheight", Integer.valueOf(headHeight));
        addConfig("navbarheight", Integer.valueOf(navbarHeight));
        addConfig(GridConfig.PROPERTITY_TITLE, gc.getTitle());
        map.put(GridConfig.PROPERTITY_TITLE, gc.getTitle());
        map.put(ROW_HEIGHT,Integer.valueOf(rowHeight));
        map.put("type","hap");
        processRowNumber(map,view);
        processSelectable(map,view);
        createGridColumns(map,view,session,model);
        if(hasFooterBar)creatFooterBar(session, context);
        map.put("gridstyle", style);
        createGridEditors(session,context);
    }


    @SuppressWarnings("unchecked")
    private void processRowNumber(Map map,CompositeMap view){
        GridConfig gc = GridConfig.getInstance(view);
        Boolean showRowNumber = gc.isShowRowNumber();
        map.put(GridConfig.PROPERTITY_SHOW_ROWNUMBER, showRowNumber);
        addConfig(GridConfig.PROPERTITY_SHOW_ROWNUMBER, showRowNumber);
    }

    @SuppressWarnings("unchecked")
    private void processSelectable(Map map,CompositeMap view){
        GridConfig gc = GridConfig.getInstance(view);
        Boolean selectable = view.getBoolean(DataSetConfig.PROPERTITY_SELECTABLE,false);
        Boolean showCheckAll = view.getBoolean(DataSetConfig.PROPERTITY_SHOW_CHECKALL,true);
        String selectionmodel = view.getString(DataSetConfig.PROPERTITY_SELECTION_MODEL,"multiple");
        CompositeMap root = view.getRoot();
        List list = CompositeUtil.findChilds(root, "dataSet");
        if(list!=null){
            String dds = gc.getBindTarget();
            Iterator it = list.iterator();
            while(it.hasNext()){
                CompositeMap ds = (CompositeMap)it.next();
                String id = ds.getString(ComponentConfig.PROPERTITY_ID, "");
                if("".equals(id)) {
                    id= IDGenerator.getInstance().generate();
                }
                if(id.equals(dds)){
                    selectable = Boolean.valueOf(ds.getBoolean(DataSetConfig.PROPERTITY_SELECTABLE, false));
                    showCheckAll = Boolean.valueOf(ds.getBoolean(DataSetConfig.PROPERTITY_SHOW_CHECKALL, true));
                    selectionmodel = ds.getString(DataSetConfig.PROPERTITY_SELECTION_MODEL, "multiple");
                    break;
                }
            }

        }
        map.put(DataSetConfig.PROPERTITY_SELECTABLE, selectable);
        map.put(DataSetConfig.PROPERTITY_SHOW_CHECKALL, showCheckAll);
        map.put(DataSetConfig.PROPERTITY_SELECTION_MODEL, selectionmodel);
        addConfig(DataSetConfig.PROPERTITY_SELECTABLE, selectable);
        addConfig(DataSetConfig.PROPERTITY_SELECTION_MODEL, selectionmodel);
    }

    @SuppressWarnings("unchecked")
    private void createGridColumns(Map map, CompositeMap view,BuildSession session,CompositeMap model){
        JSONArray jsons = new JSONArray();
        List cols = new ArrayList();
        Map lkpro = new HashMap();
        lkpro.put(LOCK_WIDTH, new Integer(0));

        lkpro.put(ROW_SPAN, new Integer(1));
        Map ukpro = new HashMap();
        ukpro.put(ROW_SPAN, new Integer(1));

        CompositeMap columns = view.getChild(GridConfig.PROPERTITY_COLUMNS);

        List lks = new ArrayList();
        List uks = new ArrayList();

        List locks = new ArrayList();
        List unlocks = new ArrayList();
        int maxRow =1;
        int rowcheckwidth = isBestseller?70:25;
        if(isHap){
            rowcheckwidth = 39;
        }
        Integer height = (Integer)map.get(TABLE_HEIGHT);

        Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);
        Integer viewWidth = (Integer)map.get(ComponentConfig.PROPERTITY_OLD_WIDTH);
        float bl = 1;
        //TODO:判断,如果column的宽度之和小于总宽度就同比放大
        GridConfig gc = GridConfig.getInstance(view);
        boolean isAutoAdjust = gc.isAutoAdjust(mDefaultAutoAdjustGrid);
        String bindTarget = gc.getBindTarget();//view.getString(ComponentConfig.PROPERTITY_BINDTARGET);
        if(isAutoAdjust)
            if(viewWidth!=null && viewWidth.intValue() !=0) bl = (width.floatValue()/viewWidth.floatValue());


        if(columns != null) {
            boolean showRowNumber = ((Boolean)map.get(GridConfig.PROPERTITY_SHOW_ROWNUMBER)).booleanValue();
            if(showRowNumber) {
                CompositeMap column = new CompositeMap("column");
                column.setNameSpaceURI(LeafApplication.LEAF_FRAMEWORK_NAMESPACE);
                column.putBoolean(GridColumnConfig.PROPERTITY_LOCK,true);
                column.putInt(ComponentConfig.PROPERTITY_WIDTH, isBestseller||isHlsDefault?70:35);
                column.putString(GridColumnConfig.PROPERTITY_ALIGN, "center");
                column.putBoolean(GridColumnConfig.PROPERTITY_RESIZABLE,false);
                column.putBoolean(GridColumnConfig.PROPERTITY_SORTABLE,false);
                column.putString(GridColumnConfig.PROPERTITY_PROMPT,isBestseller?"序号":"#");
                column.putString(GridColumnConfig.PROPERTITY_RENDERER, "Leaf.RowNumberRenderer");
                column.putString(COLUMN_TYPE,TYPE_ROW_NUMBER);
                lks.add(column);
            }

            boolean selectable = ((Boolean)map.get(DataSetConfig.PROPERTITY_SELECTABLE)).booleanValue();
            String selectmodel = (String)map.get(DataSetConfig.PROPERTITY_SELECTION_MODEL);
            if(selectable) {
                CompositeMap column = new CompositeMap("column");
                column.setNameSpaceURI(LeafApplication.LEAF_FRAMEWORK_NAMESPACE);
                column.putBoolean(GridColumnConfig.PROPERTITY_LOCK,true);
                column.putInt(ComponentConfig.PROPERTITY_WIDTH, rowcheckwidth);
                column.putBoolean(GridColumnConfig.PROPERTITY_RESIZABLE,false);
                column.putBoolean(GridColumnConfig.PROPERTITY_SORTABLE,false);
                if("multiple".equals(selectmodel)) {
                    column.putString(COLUMN_TYPE,TYPE_ROW_CHECKBOX);
                }else{
                    column.putString(COLUMN_TYPE,TYPE_ROW_RADIO);
                }
                lks.add(column);
            }

            Iterator cit = columns.getChildIterator();
            while(cit != null && cit.hasNext()){
                CompositeMap column = (CompositeMap)cit.next();
                boolean isLock = column.getBoolean(GridColumnConfig.PROPERTITY_LOCK, false);
                if(isLock){
                    lks.add(column);
                }else{
                    uks.add(column);
                }
            }

            processColumns(null, lks, locks, lkpro);
            processColumns(null, uks, unlocks, ukpro);
            int lr = ((Integer)lkpro.get(ROW_SPAN)).intValue();
            int ur = ((Integer)ukpro.get(ROW_SPAN)).intValue();

            if(ur >= lr){
                maxRow = ur;
                ukpro.put(MAX_ROWS, new Integer(maxRow));
                ukpro.put(ROW_HEIGHT, new Integer(headHeight));
                lkpro.put(MAX_ROWS, new Integer(maxRow));
                lkpro.put(ROW_HEIGHT, lr == 0 ? new Integer(headHeight) : new Integer(ur*headHeight/lr));
            } else{
                maxRow = lr;
                lkpro.put(MAX_ROWS, new Integer(maxRow));
                ukpro.put(MAX_ROWS, new Integer(maxRow));
                lkpro.put(ROW_HEIGHT, new Integer(headHeight));
                ukpro.put(ROW_HEIGHT, ur == 0 ? new Integer(headHeight) : new Integer(lr*headHeight/ur));
            }


            List lkFirstList = (List)lkpro.get("l1");
            if(lkFirstList!=null) {
                Iterator lfit = lkFirstList.iterator();
                while(lfit.hasNext()){
                    CompositeMap column = (CompositeMap)lfit.next();
                    column.put(ROW_SPAN, lkpro.get(MAX_ROWS));
                    addRowSpan(column);
                }
            }


            List ukFirstList = (List)ukpro.get("l1");
            if(ukFirstList!=null) {
                Iterator ufit = ukFirstList.iterator();
                while(ufit.hasNext()){
                    CompositeMap column = (CompositeMap)ufit.next();
                    column.put(ROW_SPAN, ukpro.get(MAX_ROWS));
                    addRowSpan(column);
                }
            }
            cols.addAll(locks);
            cols.addAll(unlocks);
            Iterator it = cols.iterator();
            while(it.hasNext()){
                CompositeMap column = (CompositeMap)it.next();
                if(column.getChilds() == null){
                    GridColumnConfig gcc = GridColumnConfig.getInstance(column);
                    String dataindex = gcc.getName();
                    if(!"".equals(dataindex))
                        column.putString(GridColumnConfig.PROPERTITY_NAME, dataindex);
                    column.putBoolean(GridColumnConfig.PROPERTITY_LOCK, gcc.isLock());
                    boolean hidden = gcc.isHidden();
                    if(hidden)
                        column.putBoolean(GridColumnConfig.PROPERTITY_VISIABLE, false);
                    if(gcc.isGroup())
                        column.putBoolean(GridColumnConfig.PROPERTITY_GROUP, true);
                    if(gcc.isShowTitle(model))
                        column.putBoolean(GridColumnConfig.PROPERTITY_SHOW_TITLE, true);
                    column.putBoolean(GridColumnConfig.PROPERTITY_HIDDEN, hidden);
                    column.putBoolean(GridColumnConfig.PROPERTITY_RESIZABLE, gcc.isResizable());
                    column.putBoolean(GridColumnConfig.PROPERTITY_SORTABLE, gcc.isSortable());
                    column.putBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, gcc.isForExport(!hidden && column.getString(COLUMN_TYPE)==null?true:false));
                    boolean autoAdjust = gcc.isAutoAdjust(mDefaultAutoAdjustGridColumn);
                    if(autoAdjust)
                        column.putBoolean(GridColumnConfig.PROPERTITY_AUTO_ADJUST, true);
                    column.putInt(GridColumnConfig.PROPERTITY_MAX_ADJUST_WIDTH, gcc.getMaxAdjustWidth());
                    column.putString(GridColumnConfig.PROPERTITY_PROMPT,session.getLocalizedPrompt(TextParser.parse(column.getString(GridColumnConfig.PROPERTITY_PROMPT,getFieldPrompt(session, column, bindTarget)),model)));
                    column.putString(GridColumnConfig.PROPERTITY_FULL_PROMPT,session.getLocalizedPrompt(TextParser.parse(column.getString(GridColumnConfig.PROPERTITY_FULL_PROMPT,getFieldFullPrompt(session, column, bindTarget)),model)));
                    String editorFunction = gcc.getEditorFunction();
                    if(editorFunction!=null)
                        column.put(GridColumnConfig.PROPERTITY_EDITOR_FUNCTION, TextParser.parse(editorFunction, model));
                    float cwidth = gcc.getWidth();
                    String type = column.getString(COLUMN_TYPE);
                    if(!"rowcheck".equals(type) && !"rowradio".equals(type)&& !"rownumber".equals(type) && autoAdjust)
                        cwidth = cwidth * bl;
                    column.putInt(GridColumnConfig.PROPERTITY_WIDTH, Math.round(cwidth));
                    String editor = gcc.getEditor(model);
                    if(!"".equals(editor)){
                        if(isCheckBoxEditor(editor, view)){
                            column.putString(COLUMN_TYPE, TYPE_CELL_CHECKBOX);
                        }
                        column.put(GridColumnConfig.PROPERTITY_EDITOR, editor);
                    }
                    String renderer = gcc.getRenderer(model);
                    if(!"".equals(renderer))
                        column.put(GridColumnConfig.PROPERTITY_RENDERER, renderer);
                    String footerRenderer = gcc.getFooterRenderer(model);
                    if(!"".equals(footerRenderer))
                        column.put(GridColumnConfig.PROPERTITY_FOOTER_RENDERER, footerRenderer);
                    String exportDataType = gcc.getExportDataType(model);
                    if(!"".equals(exportDataType))
                        column.put(GridColumnConfig.PROPERTITY_EXPORT_DATA_TYPE, exportDataType);
                    if(gcc.isFilterable()){
                        hasFilter = true;
                        column.put(GridColumnConfig.PROPERTITY_FILTERABLE, true);
                    }
                    toJSONForParentColumn(column,session,model,bindTarget);
                    JSONObject json = new JSONObject(column);
                    jsons.put(json);
                }
            }
        }

//		map.put(ComponentConfig.PROPERTITY_BINDTARGET, bindTarget);
        map.put(HEAD_HEIGHT, new Integer(maxRow*headHeight));
        map.put(LOCK_COLUMNS, locks);
        map.put(UNLOCK_COLUMNS, unlocks);
        map.put(HTML_LOCKAREA, generateLockArea(map, locks, lkpro,session, bindTarget,model));
        map.put(HTML_UNLOCKAREA, generateUnlockArea(map, unlocks, ukpro,session, bindTarget,model));
        Integer lockWidth = (Integer)lkpro.get(LOCK_WIDTH);
        Integer unlockWidth = (Integer)ukpro.get(UNLOCK_WIDTH);
        map.put(LOCK_WIDTH, lockWidth);
        map.put(UNLOCK_WIDTH, new Integer(width.intValue()-lockWidth.intValue()));
        if(isBestseller){
            map.put("unlockheadwidth", new Integer(width.intValue() - lockWidth.intValue()-(lockWidth.intValue() == 0 ?2:1)));
            map.put("unlockbodywidth", new Integer(width.intValue() - lockWidth.intValue()-2));
        }
        int bodyHeight = height.intValue()-maxRow*headHeight;
        if(hasFilter && isAutoShowFilter){
            bodyHeight -= headHeight;
        }
        if((session.getTheme().toUpperCase().indexOf("HAP") != -1)&&hasFooterBar){
            bodyHeight = bodyHeight-1;
        }
        map.put(BODY_HEIGHT, new Integer(bodyHeight));
        map.put(ROW_WIDTH,lockWidth + unlockWidth);
        addConfig(GridConfig.PROPERTITY_COLUMNS, jsons);
        addConfig(ComponentConfig.PROPERTITY_WIDTH, map.get(ComponentConfig.PROPERTITY_WIDTH));
        addConfig(ComponentConfig.PROPERTITY_HEIGHT, map.get(ComponentConfig.PROPERTITY_HEIGHT));
        addConfig("rowcheckwidth", rowcheckwidth);
        map.put(CONFIG, getConfigString());
    }


    private void toJSONForParentColumn(CompositeMap column,BuildSession session,CompositeMap model,String bindTarget){
        CompositeMap parent=null;
        if(column.get("_parent") instanceof CompositeMap){
            parent=(CompositeMap) column.get("_parent");
            if(parent!=null){
                if(!parent.getBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, true))
                    parent.putBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, parent.getBoolean(GridColumnConfig.PROPERTITY_FOR_EXPORT, true));
                parent.putString(GridColumnConfig.PROPERTITY_PROMPT,session.getLocalizedPrompt(TextParser.parse(parent.getString(GridColumnConfig.PROPERTITY_PROMPT,getFieldPrompt(session, column, bindTarget)),model)));
                parent.putString(GridColumnConfig.PROPERTITY_FULL_PROMPT,session.getLocalizedPrompt(TextParser.parse(parent.getString(GridColumnConfig.PROPERTITY_FULL_PROMPT,getFieldFullPrompt(session, column, bindTarget)),model)));
                toJSONForParentColumn(parent,session,model,bindTarget);
                column.put("_parent", new JSONObject(parent));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void createGridEditors(BuildSession session, ViewContext context) throws IOException{
        CompositeMap view = context.getView();
        Map map = context.getMap();
        CompositeMap model = context.getModel();
        CompositeMap editors = view.getChild(GridConfig.PROPERTITY_EDITORS);
        StringBuilder sb = new StringBuilder();
        if(hasFilter){
            CompositeMap filterEditor = new CompositeMap("textField");
            filterEditor.setNameSpaceURI(LeafApplication.LEAF_FRAMEWORK_NAMESPACE);
            filterEditor.put(TextFieldConfig.PROPERTITY_ID, map.get(GridConfig.PROPERTITY_ID)+"_filter_editor");
            if(editors == null){
                editors = view.createChild(GridConfig.PROPERTITY_EDITORS);
            }
            editors.addChild(filterEditor);
        }
        if(editors != null && editors.getChilds() != null) {
            Iterator it = editors.getChildIterator();
            while(it.hasNext()){
                CompositeMap editor = (CompositeMap)it.next();
                editor.put(ComponentConfig.PROPERTITY_IS_CUST, Boolean.FALSE);
                editor.put(ComponentConfig.PROPERTITY_STYLE, "position:absolute;z-index:2;left:-1000px;top:-1000px;");
                try {
                    sb.append(session.buildViewAsString(model, editor));
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
        }
        map.put("editors", sb.toString());
    }

    @SuppressWarnings("unchecked")
    private boolean isCheckBoxEditor(String id, CompositeMap view){
        boolean isChecBox = false;
        CompositeMap editors = view.getChild(GridConfig.PROPERTITY_EDITORS);
        if(editors != null && editors.getChilds() != null) {
            Iterator it = editors.getChildIterator();
            while(it.hasNext()){
                CompositeMap editor = (CompositeMap)it.next();
                String eid = editor.getString(ComponentConfig.PROPERTITY_ID,"");
                if(id.equals(eid)&& "checkBox".equals(editor.getName())){
                    isChecBox = true;
                    break;
                }
            }
        }
        return isChecBox;
    }

    @SuppressWarnings("unchecked")
    private boolean creatToolBar(BuildSession session, ViewContext context) throws IOException{
        CompositeMap view = context.getView();
        Map map = context.getMap();
        CompositeMap model = context.getModel();

        CompositeMap toolbar = view.getChild(GridConfig.PROPERTITY_TOOLBAR);
        String dataset = (String)map.get(ComponentConfig.PROPERTITY_BINDTARGET);

        StringBuilder sb = new StringBuilder();
        boolean hasToolBar = false;
        String title = (String) view.get(GridConfig.PROPERTITY_TITLE);
        if(!"false".equalsIgnoreCase(ConfigUtils.getProp(LEAF_EXCEL_BUTTON))) {
            if (toolbar == null || toolbar.getChilds() == null) {
                toolbar = new CompositeMap(GridConfig.PROPERTITY_TOOLBAR);
                CompositeMap excelBtn = new CompositeMap(COMPONENT_BUTTON);
                excelBtn.setNameSpaceURI(LeafApplication.LEAF_FRAMEWORK_NAMESPACE);
                excelBtn.put(COLUMN_TYPE, TYPE_EXCEL);
                excelBtn.setName(COMPONENT_BUTTON);
                toolbar.addChild(excelBtn);
            } else {
                List<CompositeMap> toolbarChilds = toolbar.getChilds();
                boolean excelFlag = false;
                for (int i = 0; i < toolbarChilds.size(); i++) {
                    if (toolbarChilds.get(i) != null && toolbarChilds.get(i).get("type") != null && TYPE_EXCEL.equalsIgnoreCase(toolbarChilds.get(i).get("type").toString())) {
                        excelFlag = true;
                    }
                }
                if (!excelFlag) {
                    CompositeMap excelBtn = new CompositeMap(COMPONENT_BUTTON);
                    excelBtn.setNameSpaceURI(LeafApplication.LEAF_FRAMEWORK_NAMESPACE);
                    excelBtn.put(COLUMN_TYPE, TYPE_EXCEL);
                    excelBtn.setName(COMPONENT_BUTTON);
                    toolbar.addChild(excelBtn);
                }
            }
        }
        if((toolbar != null && toolbar.getChilds() != null)||title!=null){
            hasToolBar = true;
            CompositeMap tb = new CompositeMap(GridConfig.PROPERTITY_TOOLBAR);
            tb.setNameSpaceURI(LeafApplication.LEAF_FRAMEWORK_NAMESPACE);
//			String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, ""+getDefaultWidth(BuildSession session)());
//			String wstr = TextParser.parse(widthStr, model);
//			Integer width = Integer.valueOf("".equals(wstr) ?  "150" : wstr);
            Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);

            tb.put(ComponentConfig.PROPERTITY_ID, map.get(ComponentConfig.PROPERTITY_ID)+"_tb");
            if(isHap){
                tb.put(ComponentConfig.PROPERTITY_WIDTH,width);
                tb.put(ComponentConfig.PROPERTITY_HEIGHT, -1);
            }else {
                tb.put(ComponentConfig.PROPERTITY_WIDTH, Integer.valueOf(isHlsDefault ? 34 : -1));
                tb.put(ComponentConfig.PROPERTITY_HEIGHT, Integer.valueOf(isHlsDefault ? -1 : 25));
            }
            tb.put(ComponentConfig.PROPERTITY_CLASSNAME, "grid-toolbar");
            if(title!=null){
                CompositeMap titleDiv = new CompositeMap("div");
                titleDiv.put("class","grid-toolbar-title");
                titleDiv.setText(title);
                tb.addChild(titleDiv);
            }
            if(toolbar != null && toolbar.getChilds() != null) {
                Iterator it = toolbar.getChildIterator();
                while(it.hasNext()){
                    CompositeMap item = (CompositeMap)it.next();
//				item.put(ComponentConfig.PROPERTITY_IS_CUST, Boolean.FALSE);
                    if("button".equals(item.getName())){
                        String type = item.getString("type");
                        String fileName = TextParser.parse(item.getString("filename",""),model);
                        String path = model.getObject("/request/@context_path").toString();
                        String imagePath = null;
                        String buttonTextstyle=null;
                        String iconStyle=null;
                        String btnStyle = null;
                        String topButtonStyle=null;
                        if(type!=null){
                            if(isHap){
                                imagePath = path+"/resources/leaf.ui.std/"+session.getTheme().toLowerCase()+"/button/";
                                imagePath = imagePath+"hlsgrid"+type.toLowerCase()+".png";
                                iconStyle = "line-height: 24px;height: 24px;width:24px;font-size: 12px;background-repeat:no-repeat;background-position: center;background-image:url("+imagePath+");float:left;";
                                buttonTextstyle = "margin-left: 3px;font-size: 12px;text-align: left;background-image: none;";
                                String color = "color:#fff;";
                                String backgroundSize = "background-size: 16px;";
                                String border = "border:1px solid #889DAC!important;";
								/*if("query".equalsIgnoreCase(type)){
									color = "color:#E3B286;";
									border = "border:1px solid #E3B286!important;";
								}else if("excel".equalsIgnoreCase(type)){
									backgroundSize = "background-size: 14px;";
									color = "color:#6B93C4;";
									border = "border:1px solid #6B93C4!important;";
								}else if("save".equalsIgnoreCase(type)){
									backgroundSize = "background-size: 16px;";
									color = "color:#7FAA82;";
									border = "border:1px solid #7FAA82!important;";
								}else if("delete".equalsIgnoreCase(type)){
									color = "color:#DF7979;";
									border = "border:1px solid #DF7979!important;";
								}else if("add".equalsIgnoreCase(type)){
									color = "color:#889DAC;";
									border = "border:1px solid #889DAC!important;";
								}else if("clear".equalsIgnoreCase(type)){
									color = "color:#ECCCB8;";
									border = "border:1px solid #ECCCB8!important;";
								}else{
									color = "color:#889DAC;";
								}*/
                                buttonTextstyle = buttonTextstyle+color;
                                iconStyle = iconStyle+color+backgroundSize;
                                /*topButtonStyle = "padding-left:2px;padding-right:5px;"+ border;*/
                                topButtonStyle = "padding-left:2px;padding-right:5px;background: #5A7FFF;border-radius: 2px;";
                            }
                            if("query".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_QUERY"),"grid-btn grid-query grid-btn-hap-toolbar",buttonTextstyle,"",iconStyle,btnStyle,topButtonStyle);
                            }else if("filter".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_NEW"),"grid-btn grid-filter grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').toggleFilter()}",iconStyle,btnStyle,topButtonStyle);
                            }else if("add".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_NEW"),"grid-btn grid-add grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+dataset+"').create()}",iconStyle,btnStyle,topButtonStyle);
                            }else if("delete".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_DELETE"),"grid-btn grid-delete grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').remove()}",iconStyle,btnStyle,topButtonStyle);
                            }else if("save".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_SAVE"),"grid-btn grid-save grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+dataset+"').submit()}",iconStyle,btnStyle,topButtonStyle);
                            }else if("clear".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_CLEAR"),"grid-btn grid-clear grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').clear()}",iconStyle,btnStyle,topButtonStyle);
                            }else if("excel".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_EXPORT"),"grid-btn grid-excel grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"')._export('xls','"+fileName+"')}",iconStyle,btnStyle,topButtonStyle);
                            }else if("excelmemo".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_EXPORT"),"grid-btn grid-excel grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"')._export('xls_memory','"+fileName+"')}",iconStyle,btnStyle,topButtonStyle);
                            }else if("excel2007".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_EXPORT"),"grid-btn grid-excel2007 grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"')._export('xlsx','"+fileName+"')}",iconStyle,btnStyle,topButtonStyle);
                            }else if("txt".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_EXPORT"),"grid-btn grid-txt grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"')._export('txt','"+fileName+"')}",iconStyle,btnStyle,topButtonStyle);
                            }else if("customize".equalsIgnoreCase(type)){
                                item = createButton(item,session.getLocalizedPrompt("HAP_CUST"),"grid-btn grid-cust grid-btn-hap-toolbar",buttonTextstyle,"function(){$('"+map.get(ComponentConfig.PROPERTITY_ID)+"').customize('"+path+"')}",iconStyle,btnStyle,topButtonStyle);
                            }else{
                                item = createButton(item,item.getText(),"grid-btn grid-cust grid-btn-hap-toolbar",buttonTextstyle,null,iconStyle,btnStyle,topButtonStyle);
                            }
                        }else{
                            if(isHap){
                                topButtonStyle = "padding-left:2px;padding-right:5px;background-color:#5A7FFF;border-radius: 2px;";
                                iconStyle = "line-height: 24px;height: 24px;margin-left: 2px;font-size: 12px;color:#889DAC;background-size: 18px;background-repeat:no-repeat;background-position: center;float:left;";
                                buttonTextstyle = "margin-left: 3px;font-size: 12px;text-align: center;color:#FFF;background-image: none;";
                                if(item.get(ButtonConfig.PROPERTITY_ICON_STYLE)!=null&&item.get(ButtonConfig.PROPERTITY_ICON_STYLE).toString().indexOf("background-image")!=-1){
                                    iconStyle =iconStyle+"width:24px;";
                                    buttonTextstyle =buttonTextstyle+"padding-left:20px;text-align: left;";
                                }
                                btnStyle = "color:#889DAC;";
                                item.put(ButtonConfig.PROPERTITY_ICON_STYLE,iconStyle+item.get(ButtonConfig.PROPERTITY_ICON_STYLE));
                                item.put(ButtonConfig.PROPERTITY_BUTTON_STYLE,buttonTextstyle+item.get(ButtonConfig.PROPERTITY_BUTTON_STYLE));
                                item.put(ButtonConfig.PROPERTITY_STYLE,btnStyle+item.get(ButtonConfig.PROPERTITY_STYLE));
                                item.put(ButtonConfig.PROPERTITY_BUTTON_CLASS,"grid-btn grid-cust grid-btn-hap-toolbar");
                                item.put(ButtonConfig.PROPERTITY_TOP_BUTTON_STYLE,topButtonStyle);
                            }
                        }
                    }
//					tb.addChild(item);
                    tb.addChild(0,item);
                }
                if(!isHlsDefault)sb.append("<tr><td>");
                try {
                    sb.append(session.buildViewAsString(model, tb));
                } catch (Exception e) {
                    throw new IOException(e);
                }
                if(!isHlsDefault)sb.append("</td></tr>");
            }
        }

        map.put(GridConfig.PROPERTITY_TOOLBAR, sb.toString());
        return hasToolBar;
    }


    private CompositeMap createButton(CompositeMap button, String text, String clz,String buttonTextstyle,String function){
        if("".equals(button.getString(ButtonConfig.PROPERTITY_ICON,""))){
            button.put(ButtonConfig.PROPERTITY_ICON, "null");
            button.put(ButtonConfig.PROPERTITY_BUTTON_CLASS, clz);
            button.put(ButtonConfig.PROPERTITY_BUTTON_STYLE, buttonTextstyle);
            if(isHlsDefault){
                button.put(ButtonConfig.PROPERTITY_WIDTH, 34);
                button.put(ButtonConfig.PROPERTITY_HEIGHT, 29);
            }
            if(isHap){
                button.put(ButtonConfig.PROPERTITY_WIDTH, 24);
                button.put(ButtonConfig.PROPERTITY_HEIGHT, 24);
            }
        }
        if(!isHap){
            if(isHlsDefault)
                button.put(ButtonConfig.PROPERTITY_TITLE,button.getString(ButtonConfig.PROPERTITY_TEXT, text));
            else
                button.put(ButtonConfig.PROPERTITY_TEXT,button.getString(ButtonConfig.PROPERTITY_TEXT, text));
        }else{
            button.put(ButtonConfig.PROPERTITY_TEXT,button.getString(ButtonConfig.PROPERTITY_TEXT, text));
        }
        if(!"".equals(function))button.put(ButtonConfig.PROPERTITY_CLICK, function);
        return button;
    }

    private CompositeMap createButton(CompositeMap button, String text, String clz,String buttonTextstyle,String function,String iconStyle,String btnStyle,String topButtonStyle){
        createButton(button,text, clz,buttonTextstyle,function);
        if(iconStyle!=null){
            button.put(ButtonConfig.PROPERTITY_ICON_STYLE,iconStyle);
        }
        if(btnStyle!=null){
            button.put(ButtonConfig.PROPERTITY_STYLE,(button.get(ButtonConfig.PROPERTITY_STYLE)==null?"":button.get(ButtonConfig.PROPERTITY_STYLE))+btnStyle);
        }
        if(topButtonStyle!=null){
            button.put(ButtonConfig.PROPERTITY_TOP_BUTTON_STYLE,(button.get(ButtonConfig.PROPERTITY_TOP_BUTTON_STYLE)==null?"":button.get(ButtonConfig.PROPERTITY_TOP_BUTTON_STYLE))+topButtonStyle);
        }
        return button;
    }

//	public boolean hasFooterBar(BuildSession session, ViewContext context){
//		boolean hasFooterBar = false;
//		CompositeMap view = context.getView();
//		GridConfig gc = GridConfig.getInstance(view);
//		CompositeMap columns = gc.getColumns();
//		List childs = columns.getChilds();
//		if(childs!=null){
//			Iterator it = childs.iterator();
//			while(it.hasNext()){
//				CompositeMap column = (CompositeMap)it.next();
//				GridColumnConfig gcc = GridColumnConfig.getInstance(column);
//				String footerRenderer = gcc.getFooterRenderer();
//				if(footerRenderer!=null){
//					hasFooterBar = true;
//					break;
//				}
//			}
//		}
//		return hasFooterBar;
//	}

    @SuppressWarnings("unchecked")
    public boolean hasFooterBar(CompositeMap column,CompositeMap model){
        boolean hasFooterBar = false;
        GridColumnConfig gcc = GridColumnConfig.getInstance(column);
        String footerRenderer = gcc.getFooterRenderer(model);
        if(!"".equals(footerRenderer)){
            return true;
        }
        List childs = column.getChilds();
        if(childs!=null){
            Iterator it = childs.iterator();
            while(it.hasNext()){
                CompositeMap col = (CompositeMap)it.next();
                if(hasFooterBar(col,model)){
                    hasFooterBar = true;
                    break;
                }
            }
        }
        return hasFooterBar;
    }

    @SuppressWarnings("unchecked")
    private void creatFooterBar(BuildSession session, ViewContext context) throws IOException{
        Map map = context.getMap();
        int lockWidth = ((Integer)map.get(LOCK_WIDTH)).intValue();
        StringBuilder sb = new StringBuilder();
        sb.append("<tr><td><div class='grid-footerbar' atype='grid.fb"+"' style='width:"+map.get(ComponentConfig.PROPERTITY_WIDTH)+"px'>");
        if(lockWidth!=0){
            sb.append("<div atype='grid.lf' style='float:left;width:"+(lockWidth-1)+"px'>");//class='grid-la'
            List locks = (List)map.get(LOCK_COLUMNS);
            if(locks!=null){
                sb.append("<table cellSpacing='0' cellPadding='0' border='0' atype='fb.lbt' ");
                Iterator it = locks.iterator();
                sb.append(createFooterBarTable(it,false));
            }
            sb.append("</div>");
        }

        sb.append("<div class='grid-ua' atype='grid.uf' style='width:"+map.get(UNLOCK_WIDTH)+"px'>");
        List unlocks = (List)map.get(UNLOCK_COLUMNS);
        if(unlocks!=null){
            sb.append("<table cellSpacing='0' cellPadding='0' border='0' atype='fb.ubt' ");
            Iterator it = unlocks.iterator();
            sb.append(createFooterBarTable(it,true));
        }
        sb.append("</div>");
        sb.append("</div></td></tr>");
        map.put(FOOTER_BAR, sb.toString());
    }

    @SuppressWarnings("unchecked")
    private String createFooterBarTable(Iterator it,boolean hasSpan){
        int i = 0,w = 0;
        StringBuilder sb = new StringBuilder();
        StringBuilder tb = new StringBuilder();
        StringBuilder th = new StringBuilder();
        th.append("<tr class='grid-hl'>");
        while(it.hasNext()){
            CompositeMap column = (CompositeMap)it.next();
            if(column.getChilds()!=null) continue;
            GridColumnConfig gcc = GridColumnConfig.getInstance(column);
            w += gcc.getWidth();
            th.append("<th style='width:"+gcc.getWidth()+"px;' dataindex='"+gcc.getName()+"'></th>");
            tb.append("<td style='text-align:"+gcc.getAlign()+";");
//			tb.append(((i==0) ? "border:none;" : "")+"'");
            tb.append("'");
            if(!"".equals(gcc.getName())) tb.append("dataindex='"+gcc.getName()+"'");
            tb.append(">&#160;</td>");
            i++;
        }
        if(hasSpan)th.append("<th style='width:34px;'></th>");
        sb.append("style='width:"+w+"px;table-layout:fixed;'>");//margin-right:20px;padding-right:20px;
        sb.append(th.toString()).append("</tr><tr>");

        sb.append(tb.toString());
        sb.append("</tr></table>");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private boolean createNavgationToolBar(BuildSession session, ViewContext context) throws IOException{
        boolean hasNavBar = false;
        CompositeMap view = context.getView();
        GridConfig gc = GridConfig.getInstance(view);

        Map map = context.getMap();
        CompositeMap model = context.getModel();
        StringBuilder sb = new StringBuilder();
        String dataset = (String) map.get(ComponentConfig.PROPERTITY_BINDTARGET);

        boolean hasNav = gc.hasNavBar(model);
        if(hasNav){
            hasNavBar = true;
            CompositeMap navbar = new CompositeMap("navBar");
            navbar.setNameSpaceURI(LeafApplication.LEAF_FRAMEWORK_NAMESPACE);
//			String widthStr = view.getString(ComponentConfig.PROPERTITY_WIDTH, ""+getDefaultWidth(BuildSession session)());
//			String wstr = TextParser.parse(widthStr, model);
            Integer width = (Integer)map.get(ComponentConfig.PROPERTITY_WIDTH);//Integer.valueOf("".equals(wstr) ?  "150" : wstr);


//			Integer width = Integer.valueOf(view.getString(ComponentConfig.PROPERTITY_WIDTH));
            navbar.put(ComponentConfig.PROPERTITY_ID, map.get(ComponentConfig.PROPERTITY_ID)+"_navbar");
            navbar.put(ComponentConfig.PROPERTITY_IS_CUST, Boolean.FALSE);
            navbar.put(ComponentConfig.PROPERTITY_HEIGHT, new Integer(navbarHeight));
            navbar.put(ComponentConfig.PROPERTITY_CLASSNAME, "grid-navbar");
//			navbar.put(PROPERTITY_STYLE, "border:none;border-top:1px solid #cccccc;");
            navbar.put(NavBarConfig.PROPERTITY_DATASET, dataset);
            DataSetConfig dc = DataSetConfig.getInstance(getDataSet(session, dataset));
            boolean autoCount = dc.isAutoCount(model,mDefaultAutoCount);
            boolean infiniteLoad = dc.isInfiniteLoad(mDefaultInfiniteLoad);
            navbar.put(NavBarConfig.PROPERTITY_NAVBAR_TYPE, view.getString(NavBarConfig.PROPERTITY_NAVBAR_TYPE,infiniteLoad?"infinite":autoCount?"complex":"tiny"));
            navbar.put(NavBarConfig.PROPERTITY_MAX_PAGE_COUNT, new Integer(view.getInt(NavBarConfig.PROPERTITY_MAX_PAGE_COUNT,10)));
            navbar.put(NavBarConfig.PROPERTITY_PAGE_SIZE_EDITABLE,Boolean.valueOf(view.getBoolean(NavBarConfig.PROPERTITY_PAGE_SIZE_EDITABLE,true)));
            sb.append("<tr><td class='grid-navbar-td'>");
            try {
                sb.append(session.buildViewAsString(model, navbar));
            } catch (Exception e) {
                throw new IOException(e);
            }
            sb.append("</td></tr>");
            map.put("navbar", sb.toString());
        }
        return hasNavBar;
    }


    @SuppressWarnings("unchecked")
    private void processColumns(CompositeMap parent, List children, List cols, Map pro){
        Iterator it = children.iterator();
        boolean plock =parent!=null? (parent.getBoolean(GridColumnConfig.PROPERTITY_LOCK) != null ? parent.getBoolean(GridColumnConfig.PROPERTITY_LOCK).booleanValue() : false ):false;
        while(it.hasNext()){
            CompositeMap column = (CompositeMap)it.next();
            if(plock)
                column.putBoolean(GridColumnConfig.PROPERTITY_LOCK, true);
            int level;
            if(parent == null){
                level = 1;
            }else{
                level = parent.getInt("_level").intValue() + 1;
            }
            int rows = ((Integer)pro.get(ROW_SPAN)).intValue();
            if(level>rows)pro.put(ROW_SPAN, new Integer(level));

            column.put("_level", new Integer(level));
            column.put("_parent", parent);
            List hlist = (List)pro.get("l"+level);
            if(hlist == null){
                hlist = new ArrayList();
                pro.put("l"+level, hlist);
            }
            hlist.add(column);
            cols.add(column);
            if(column.getChilds() != null && column.getChilds().size() >0){
                processColumns(column, column.getChilds(), cols, pro);
            }else{
                addColSpan(column);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addRowSpan(CompositeMap column){
        List children = column.getChilds();
        Integer psp = column.getInt(ROW_SPAN);
        if(children != null && children.size() >0){
            minusRowSpan(column);
            Iterator it = children.iterator();
            while(it.hasNext()){
                CompositeMap child = (CompositeMap)it.next();
                child.put(ROW_SPAN, new Integer(psp.intValue()-1));
                addRowSpan(child);
            }
        }

    }

    private void minusRowSpan(CompositeMap column){
        if(column == null)return;
        Integer rowspan = column.getInt(ROW_SPAN);
        if(rowspan != null){
            int cs = Math.max(rowspan.intValue() -1,1);
            column.put(ROW_SPAN, new Integer(cs));
        }
        CompositeMap parent = (CompositeMap)column.get("_parent");
        if(parent != null){
            minusRowSpan(parent);
        }

    }


    private void addColSpan(CompositeMap column){
        if(column == null)return;
        CompositeMap parent = (CompositeMap)column.get("_parent");
        if(parent != null){
            Integer colspan = parent.getInt(COL_SPAN);
            if(colspan == null){
                parent.put(COL_SPAN, new Integer(1));
            }else{
                int cs = colspan.intValue() +1;
                parent.put(COL_SPAN, new Integer(cs));
            }
        }
        addColSpan(parent);
    }


    @SuppressWarnings("unchecked")
    private String generateLockArea(Map map, List columns, Map pro,BuildSession session, String dataSet, CompositeMap model){
        StringBuilder sb = new StringBuilder();
        StringBuilder th = new StringBuilder();
        StringBuilder filter = new StringBuilder("");
        boolean hasLockColumn = false;
        Integer rows = (Integer)pro.get(ROW_SPAN);
        Iterator it = columns.iterator();
        int lockWidth = 0;
        int row_height = ((Integer) pro.get(ROW_HEIGHT))==null?0:((Integer) pro.get(ROW_HEIGHT)).intValue();
        while(it.hasNext()){
            CompositeMap column = (CompositeMap)it.next();
            GridColumnConfig gcc = GridColumnConfig.getInstance(column);
            if(gcc.isLock()){
                hasLockColumn = true;
                List children = column.getChilds();
                if(children == null){
                    boolean hidden = gcc.isHidden();
                    if(hidden) continue;
                    float cwidth = hidden? 0 : gcc.getWidth();
                    th.append("<th style='width:"+cwidth+"px;' dataindex='"+gcc.getName()+"'></th>");
                    lockWidth +=cwidth;
                }
            }
        }

        pro.put(LOCK_WIDTH, new Integer(lockWidth));

        if(hasLockColumn){
            sb.append("<DIV class='grid-la' atype='grid.lc' style='width:"+(lockWidth-1)+"px;'>")
                    .append("<DIV class='grid-lh' atype='grid.lh' unselectable='on' onselectstart='return false;' style='height:"+rows.intValue()*((Integer)pro.get(ROW_HEIGHT)).intValue()+"px;'>");
            filter.append("<div class='grid-lfi' atype='grid.lfi' style='width:"+(lockWidth-1)+"px;");
            filter.append("'>");
            StringBuilder hsb = new StringBuilder();
            int finnalRowHeight = row_height;
            filter.append("<table cellSpacing='0' atype='grid.lfit' cellPadding='0' border='0' style='width:"+lockWidth+"px'><TBODY>")
                    .append("<tr class='grid-hl'>")
                    .append(th.toString())
                    .append("</tr>")
                    .append("<tr style=\"height:"+finnalRowHeight+"px;line-height:"+(row_height-6)+"px\">");
            for(int i=1,len = rows.intValue();i<=len;i++){
                List list = (List)pro.get("l"+i);
                hsb.append("<TR style=\"height:"+finnalRowHeight+"px;line-height:"+(row_height-6)+"px\">");
                if(list!=null) {
                    Iterator lit = list.iterator();
                    while(lit.hasNext()){
                        CompositeMap column = (CompositeMap)lit.next();
                        GridColumnConfig gcc = GridColumnConfig.getInstance(column);
                        String ct = column.getString(COLUMN_TYPE);
                        boolean showCheckAll = isBestseller?false:((Boolean) map.get(DataSetConfig.PROPERTITY_SHOW_CHECKALL)).booleanValue();
                        if(TYPE_ROW_CHECKBOX.equals(ct)){
                            if(showCheckAll){
                                hsb.append("<TD class='grid-hc' atype='grid.rowcheck' style='height:"+finnalRowHeight+"px;' rowspan='"+column.getInt(ROW_SPAN)+"'><div atype='grid.headcheck' style='margin:auto' class='grid-ckb item-ckb-u'></div></TD>");
                            }else{
                                hsb.append("<TD class='grid-hc' atype='grid.rowcheck' style='height:"+finnalRowHeight+"px;' rowspan='"+column.getInt(ROW_SPAN)+"'>");
                                if(isBestseller){
                                    hsb.append("<center><div atype='grid.headcheck'>全选</div></center>");
                                }
                                hsb.append("</TD>");
                            }
                            if(i == len)
                                filter.append("<td><div class='grid-filter-icon'></div></td>");
                        }else if(TYPE_ROW_RADIO.equals(ct)) {
                            hsb.append("<TD class='grid-hc' atype='grid.rowradio' style='height:"+finnalRowHeight+"px;' rowspan='"+column.getInt(ROW_SPAN)+"'><div>&nbsp;</div></TD>");
                            if(i == len)
                                filter.append("<td></td>");
                        }else{
                            boolean hidden = gcc.isHidden();
                            if(hidden) continue;
                            String prompt = getFieldPrompt(session, column, dataSet);
                            String fullPrompt = getFieldFullPrompt(session, column, dataSet);
                            if("".equals(fullPrompt)){
                                fullPrompt = prompt;
                            }
                            String headTitle = session.getLocalizedPrompt(prompt);
                            if(headTitle!=null && headTitle.equals(prompt)){
                                headTitle = TextParser.parse(prompt, model);
                            }
                            hsb.append("<TD title='"+fullPrompt+"' class='grid-hc' atype='grid.head' style='height:"+finnalRowHeight+"px;visibility:"+(hidden?"hidden":"")+"' colspan='"+column.getInt(COL_SPAN,1)+"' rowspan='"+column.getInt(ROW_SPAN)+"' dataindex='"+gcc.getName()+"'><div");
                            if(gcc.getHeadStyle() != null){
                                hsb.append(" style='").append(gcc.getHeadStyle()).append("'");
                            }
                            hsb.append(">");
                            //必填
                            if(gcc.isRequired()){
                                hsb.append("<span class='prompt-required prompt-required-show' title='"+gcc.getName()+"'>*</span>");
                            }
                            hsb.append(headTitle);
                            if(gcc.isFilterable()){
                                hsb.append("<input class='grid-filter-icon' tabIndex='-1' disabled>");
                            }
                            //筛选
//                            if(gcc.isSortable()){
//                                hsb.append("<span class='sort-btn'></span>");
//                            }
                            hsb.append("</div></TD>");
                            if(i == len){
                                if(gcc.isFilterable())
                                    filter.append("<td atype=\"grid-filter\" dataindex=\""+gcc.getName()+"\"><div class='grid-cell cell-editor'></div></td>");
                                else
                                    filter.append("<td><div class='grid-cell'></div></td>");
                            }
                        }
                    }
                }
                hsb.append("</TR>");
            }
            filter.append("</tr>")
                    .append("</tbody></table>")
                    .append("</div>");

            sb.append("<TABLE cellSpacing='0' atype='grid.lht' cellPadding='0' border='0' style='width:"+lockWidth+"px'><TBODY>")
                    .append("<TR class='grid-hl'>")
                    .append(th.toString())
                    .append("</TR>")
                    .append(hsb)
                    .append("</TBODY></TABLE>");

            Integer height = (Integer)map.get(TABLE_HEIGHT);
            sb.append("</DIV>");
            if(hasFilter)
                sb.append(filter);
            int finnalRowHeght = (height.intValue()-rows.intValue()*((Integer)pro.get(ROW_HEIGHT)).intValue());
            if((session.getTheme().toUpperCase().indexOf("HAP") != -1)&&hasFooterBar){
                finnalRowHeght = finnalRowHeght - 1;
            }
            sb.append("<DIV class='grid-lb' atype='grid.lb' style='width:100%;height:"+finnalRowHeght+"px'>")
                    .append("</DIV></DIV>");

        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String generateUnlockArea(Map map, List columns, Map pro,BuildSession session, String dataSet, CompositeMap model){
        StringBuilder sb = new StringBuilder();
        StringBuilder th = new StringBuilder();
        StringBuilder filter = new StringBuilder("");

        Integer rows = (Integer)pro.get(ROW_SPAN);
        Iterator it = columns.iterator();
        int unlockWidth = 0;
        int row_height = ((Integer) pro.get(ROW_HEIGHT))==null?0:((Integer) pro.get(ROW_HEIGHT)).intValue();
        while(it.hasNext()){
            CompositeMap column = (CompositeMap)it.next();
            GridColumnConfig gcc = GridColumnConfig.getInstance(column);
            if(!gcc.isLock()){
                List children = column.getChilds();
                if(children == null){
                    boolean hidden = gcc.isHidden();
                    if(hidden)continue;
                    float cwidth = hidden?0:gcc.getWidth();
                    th.append("<th style='width:"+cwidth+"px;' dataindex='"+gcc.getName()+"'></th>");
                    unlockWidth +=cwidth;
                }
            }
        }
        pro.put(UNLOCK_WIDTH, new Integer(unlockWidth));
        int finnalRowHeight = row_height;
        sb.append("<table cellspacing='0' atype='grid.uht' cellpadding='0' border='0' style='width:"+unlockWidth+"px'><tbody>")
                .append("<tr class='grid-hl'>")
                .append(th.toString())
                .append("<th width='34'> </th>")
                .append("</tr>");

        filter.append("<div class='grid-ufi' atype='grid.ufi' style='width:"+(pro.get(UNLOCK_WIDTH))+"px;");
        filter.append("'>")
                .append("<table cellspacing='0' atype='grid.ufit' cellpadding='0' border='0' style='width:"+unlockWidth+"px'><tbody>")
                .append("<tr class='grid-hl'>")
                .append(th.toString())
                .append("<th width='34'> </th>")
                .append("</tr>")
                .append("<tr style=\"height:"+finnalRowHeight+"px;line-height:"+(row_height-6)+"px\">");
        StringBuilder hsb = new StringBuilder();
        for(int i=1,len = rows.intValue();i<=len;i++){
            List list = (List)pro.get("l"+i);
            hsb.append("<TR style=\"height:"+finnalRowHeight+"px;line-height:"+(row_height-6)+"px\">");
            if(list!=null) {
                Iterator lit = list.iterator();
                while(lit.hasNext()){
                    CompositeMap column = (CompositeMap)lit.next();
                    GridColumnConfig gcc = GridColumnConfig.getInstance(column);
                    boolean hidden =  gcc.isHidden();
                    if(hidden)continue;
                    String prompt = getFieldPrompt(session, column, dataSet);
                    String fullPrompt = getFieldFullPrompt(session, column, dataSet);
                    if("".equals(fullPrompt)){
                        fullPrompt = prompt;
                    }
                    String headTitle = session.getLocalizedPrompt(prompt);
                    if(headTitle!=null && headTitle.equals(prompt)){
                        headTitle = TextParser.parse(prompt, model);
                    }
                    hsb.append("<TD title='"+fullPrompt+"' class='grid-hc' atype='grid.head' style='height:"+finnalRowHeight+"px;visibility:"+(hidden?"hidden":"")+"' colspan='"+column.getInt(COL_SPAN,1)+"' rowspan='"+column.getInt(ROW_SPAN)+"' dataindex='"+gcc.getName()+"'><div");
                    if(gcc.getHeadStyle() != null){
                        hsb.append(" style='").append(gcc.getHeadStyle()).append("'");
                    }
                    hsb.append(">");
                    //必填
                    if(gcc.isRequired()){
                        hsb.append("<span class='prompt-required prompt-required-show' title='"+gcc.getName()+"'>*</span>");
                    }
                    hsb.append(headTitle);
                    if(gcc.isFilterable()){
                        hsb.append("<input class='grid-filter-icon' tabIndex='-1' disabled>");
                    }
//                    if(gcc.isSortable()){
//                        hsb.append("<span class='sort-btn'></span>");
//                    }
                    hsb.append("</div></TD>");

                    if(i == len){
                        if(gcc.isFilterable())
                            filter.append("<td atype=\"grid-filter\" dataindex=\""+gcc.getName()+"\"><div class='grid-cell cell-editor'></div></td>");
                        else
                            filter.append("<td><div class='grid-cell'></div></td>");
                    }
                }
            }
            hsb.append("</TR>");
        }
        filter.append("</tr>")
                .append("</tbody></table>").append("</div>");
        sb.append(hsb)
                .append("</TBODY></TABLE>");
        if(hasFilter)
            map.put(UNLOCK_FILTER, filter.toString());
        return sb.toString();
    }
    private String getFieldFullPrompt(BuildSession session, CompositeMap field, String dataset) {
        String label = field.getString(GridColumnConfig.PROPERTITY_FULL_PROMPT, "");
        if ("".equals(label)) {
            String name = field.getString(ComponentConfig.PROPERTITY_NAME, "");
            CompositeMap ds = getDataSet(session, dataset);
            if (ds != null) {
                CompositeMap fieldcm = ds.getChild(DataSetConfig.PROPERTITY_FIELDS);
                if (fieldcm != null) {
                    List fields = fieldcm.getChilds();
                    Iterator it = fields.iterator();
                    while (it.hasNext()) {
                        CompositeMap fieldMap = (CompositeMap) it.next();
                        String fn = fieldMap.getString(ComponentConfig.PROPERTITY_NAME, "");
                        if (name.equals(fn)) {
                            label = fieldMap.getString(GridColumnConfig.PROPERTITY_FULL_PROMPT, "");
                            break;
                        }
                    }
                }
            }
        }
        return label;
    }

}
