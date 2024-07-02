package leaf.presentation.component.std;

import leaf.presentation.BuildSession;
import leaf.presentation.component.std.config.BoxConfig;
import leaf.presentation.component.std.config.ComponentConfig;
import leaf.presentation.component.std.config.FormConfig;
import leaf.presentation.component.std.config.GridLayoutConfig;
import leaf.utils.ConfigUtils;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

import java.io.Writer;
import java.util.Map;

public class Form extends Box {
	
	public static final String VERSION = "$Revision: 1.1 $";
	
	
	private static final String DEFAULT_HEAD_CLASS = "form_head";
	private static final String DEFAULT_BODY_CLASS = "form_body";
	private static final String FLEXIBLE_BODY_CLASS = "form_body_flexible";

	private static final String FORM_FLEXIBLE = "leaf.form.flexible";

	private static final String QUERY_TITLE = "查询条件";

	public Form(IObjectRegistry registry) {
		super(registry);
	}
	
	protected String getHeadClass(){
		return DEFAULT_HEAD_CLASS;
	}
	protected String getBodyClass(){
		return DEFAULT_BODY_CLASS;
	}
	
	protected int getPadding(BuildSession session){
		if(THEME_HLS_DEFAULT.equals(session.getTheme())){
			return 0;
		}
		return super.getPadding(session);
	}
	
	protected void buildHead(BuildSession session, CompositeMap model, CompositeMap view, int rows , int columns) throws Exception{
		String theme = session.getTheme();
		if(THEME_MAC.equals(theme)){
			return;
		}
		Writer out = session.getWriter();
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		title = session.getLocalizedPrompt(uncertain.composite.TextParser.parse(title,model));
		if(!"".equals(title)) {
			out.write("<thead><tr><th class='"+getHeadClass()+"' colspan="+columns*2+"><span></s" +
					"pan>");
			out.write(title);
			if(checkFlexible(view)){
				out.write("<div class='form-bottom-flexible form-bottom-flexible-hidden' data-bind='"+view.get("id")+"'></div>");
			}
			out.write("</th></tr></thead>");
		}
	}

	private boolean checkFlexible(CompositeMap view){
		if("Y".equalsIgnoreCase(view.getString(FormConfig.PROPERTITY_FLEXIBLE,""))){
			return  true;
		}else if("N".equalsIgnoreCase(view.getString(FormConfig.PROPERTITY_FLEXIBLE,""))){
			return false;
		}else if(ConfigUtils.getBooleanProp(FORM_FLEXIBLE)){
			if(view.get("title") != null && QUERY_TITLE.equals(view.get("title"))) {
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	protected void afterBuildTop(BuildSession session, CompositeMap model, CompositeMap view, int columns) throws Exception{
		Writer out = session.getWriter();
		if(checkFlexible(view)){
			out.write("<tbody class='" + getBodyClass() + " "+FLEXIBLE_BODY_CLASS+"'>");
		}else {
			out.write("<tbody class='" + getBodyClass() + "'>");
		}
		
		String showmargin = view.getString(FormConfig.PROPERTITY_SHOWMARGIN, "true");
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, THEME_HLS_DEFAULT.equals(session.getTheme())?true:false);
		if("true".equals(showmargin) && !showBorder)out.write("<tr height='5'><td colspan="+columns*2+"></td></tr>");
		super.afterBuildTop(session, model, view,columns);
	}
	
	protected String getClassName(BuildSession session, CompositeMap model, CompositeMap view ) throws Exception{
		String cls = view.getString(ComponentConfig.PROPERTITY_CLASSNAME, "");
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, THEME_HLS_DEFAULT.equals(session.getTheme())?true:false);
		String className = DEFAULT_TABLE_CLASS + " layout-form";
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		title = session.getLocalizedPrompt(uncertain.composite.TextParser.parse(title,model));
		if(!"".equals(title)) className += " " + TITLE_CLASS;
		className += " " + cls;
		if(showBorder) {
			className += " layout-border";
		}
		return className;
	}
	
	protected String getStyle(BuildSession session, CompositeMap model, CompositeMap view ) throws Exception{
		String style = view.getString(ComponentConfig.PROPERTITY_STYLE, "");
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, THEME_HLS_DEFAULT.equals(session.getTheme())?true:false);
		if(showBorder) {
			style += " border:none;";
		}
		return style;
	}
	
	protected void beforeBuildTop(BuildSession session, CompositeMap model, CompositeMap view, String id ) throws Exception{
		String theme = session.getTheme();
		if(THEME_MAC.equals(theme)){
			String style = getStyle(session,model,view);
			Writer out = session.getWriter();
			String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
			title = session.getLocalizedPrompt(uncertain.composite.TextParser.parse(title,model));
			if(!"".equals(title)) {
				out.write("<table border='0' class='mac_form' cellpadding='0' cellSpacing='0' id='"+id+"'");
				if(!"".equals(style)) {
					out.write(" style='"+style+"'");
				}
				out.write("><tr><td class='form_head'>");
				out.write(title);
				out.write("</td></tr><tr><td>");
			}
		}
	}
	
	
	protected void buildTop(BuildSession session, CompositeMap model, CompositeMap view, Map map, int rows, int columns, String id) throws Exception{
		
		beforeBuildTop(session,model,view,id);
		if(view.get("id") == null){
			view.put("id",id);
		}
		Writer out = session.getWriter();
		int cellspacing = view.getInt(GridLayoutConfig.PROPERTITY_CELLSPACING, 0);
		int cellpadding = view.getInt(GridLayoutConfig.PROPERTITY_CELLPADDING, 0);
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, THEME_HLS_DEFAULT.equals(session.getTheme())?true:false);
		
		String title = view.getString(FormConfig.PROPERTITY_TITLE, "");
		title = session.getLocalizedPrompt(uncertain.composite.TextParser.parse(title,model));
		
		int width = getComponentWidth(session,model, view, map).intValue();
		int height = getComponentHeight(session,model, view, map).intValue();
		
		String className = getClassName(session,model,view);
		String style = getStyle(session,model,view);
		
		if(showBorder) {
			cellspacing = 1;
			className += " layout-border";
		}
		String theme = session.getTheme();
		if(THEME_MAC.equals(theme)){
			out.write("<table border=0 class='"+className+"'");
		}else{
			out.write("<table border=0 class='"+className+"' id='"+id+"'");
		}
		if(width != 0) out.write(" width=" + width);
		if(height != 0) out.write(" height=" + height);
		if(THEME_MAC.equals(theme) && !"".equals(title)){
			out.write(" style='width:100%'");
		}else {
			if(!"".equals(style)) {
				out.write(" style='"+style+"'");
			}
		}
		out.write(" cellpadding="+cellpadding+" cellspacing="+cellspacing+">");
		buildHead(session,model,view, rows, columns);
		afterBuildTop(session,model,view,columns);
	}

	
	protected void buildFoot(BuildSession session, CompositeMap model, CompositeMap view, int columns) throws Exception{
		super.buildFoot(session, model, view,columns);
		Writer out = session.getWriter();
		String showmargin = view.getString(FormConfig.PROPERTITY_SHOWMARGIN, "true");
		boolean showBorder = view.getBoolean(BoxConfig.PROPERTITY_SHOWBORDER, THEME_HLS_DEFAULT.equals(session.getTheme())?true:false);
		if("true".equals(showmargin) && !showBorder)out.write("<tr height='12'><td colspan="+columns*2+"></td></tr>");
	}
	
	protected void afterBuildBottom(BuildSession session, CompositeMap model, CompositeMap view, int columns) throws Exception{
		String theme = session.getTheme();
		FormConfig fc = new FormConfig();
		fc.initialize(view);
		String title = fc.getTitle();
		title = session.getLocalizedPrompt(title);
		if(THEME_MAC.equals(theme) && !"".equals(title)){
			Writer out = session.getWriter();
			out.write("</td></tr></table>");
		}
	}

}
