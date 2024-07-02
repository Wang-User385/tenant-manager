package leaf.presentation.component.std;


import leaf.presentation.BuildSession;
import leaf.presentation.ViewContext;
import uncertain.composite.CompositeMap;
import uncertain.ocm.IObjectRegistry;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: lrd
 * Date: 2018/5/4
 * Time: 17:29
 * To change this template use File | Settings | File Templates.
 */

public class RightNavBars extends Component {

    public RightNavBars(IObjectRegistry registry) {
        super(registry);
    }

    public void onPreparePageContent(BuildSession session, ViewContext context) throws IOException {
        super.onPreparePageContent(session, context);
        addStyleSheet(session, context, "rightnavbar/rightnavbar.css");
        addJavaScript(session, context, "rightnavbar/rightnavbar.js");
    }

    public String createHtml(String contextPath,String distance,List<CompositeMap> list){

        if(distance == null){
            distance = "20px";
        }else {
            String pattern = ".*px.*";
            boolean isMatch = Pattern.matches(pattern, distance);
            if (isMatch == false) {
                distance = distance + "px";
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append("<div class='hls-selectBar' style='right:"+distance+"'><ul>");
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                CompositeMap item = list.get(i);
                sb.append("<li>");
                sb.append("<div class='contain' id='"+item.getString("id")+"' onclick='" + item.get("click") + "()'> ");
                sb.append("  <img src='" + contextPath + "/" + item.get("defaultimage") + "'/>");
                sb.append("<div class='text-content-right'>");
                sb.append(item.get("title"));
                sb.append("</div>");
                sb.append("</div>");
                sb.append("</li>");
            }
        }
        sb.append("</ul></div>");
        return sb.toString();
    }

    public void onCreateViewContent(BuildSession session, ViewContext context) throws IOException{
        super.onCreateViewContent(session, context);
        CompositeMap view = context.getView();

        Map map = context.getMap();

        /*addConfig("rightNavBars",createHtml(view.getChilds()));*/
        map.put("id",view.get("id").toString());
        if(view.get("right") != null) {
            map.put("rightNavBars", createHtml(session.getContextPath(), view.get("right").toString(), view.getChilds()));
        }else{
            map.put("rightNavBars", createHtml(session.getContextPath(), null, view.getChilds()));
        }

        map.put(CONFIG,getConfig());
    }




}
