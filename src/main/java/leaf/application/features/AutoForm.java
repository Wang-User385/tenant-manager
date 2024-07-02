package leaf.application.features;

import com.hand.hls.layout.components.DocLayoutConfigCache;
import com.hand.hls.layout.dto.DocLayoutConfigLov;
import com.hand.hls.utils.SpringContextHolder;
import leaf.annotation.LovField;
import leaf.bm.BusinessModel;
import leaf.database.service.BusinessModelService;
import leaf.database.service.IDatabaseServiceFactory;
import leaf.presentation.component.std.config.*;
import leaf.service.ServiceContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.EventModel;
import uncertain.proc.IFeature;
import uncertain.proc.ProcedureRunner;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class AutoForm implements IFeature {
    private static final String PROPERTITY_MODEL = "model";
    private static final String PROPERTITY_DTO = "dto";
    private static final Object PROPERTITY_LOV_CACHE_KEY = "lovcachekey";

    private static final String PROPERTITY_ENTERDOWN_HANDLER = "enterdownhandler";

    IDatabaseServiceFactory mFactory;
    CompositeMap view;

    private DocLayoutConfigCache docLayoutConfigCache;
    private Logger logger = LoggerFactory.getLogger(getClass());

    {
        docLayoutConfigCache = SpringContextHolder.getBean(DocLayoutConfigCache.class);
    }

    public AutoForm(IDatabaseServiceFactory factory) {
        this.mFactory = factory;
    }

    public int onCreateView(ProcedureRunner runner) throws IOException {
        ServiceContext sc = ServiceContext.createServiceContext(runner.getContext());
        CompositeMap model = sc.getModel();

        FormConfig formConfig = FormConfig.getInstance(view);
        formConfig.setCellSpacing(0);

        String target = view.getString(ComponentConfig.PROPERTITY_BINDTARGET, "");
        String handler = view.getString(PROPERTITY_ENTERDOWN_HANDLER);
        String lovCacheKey = view.getString(PROPERTITY_LOV_CACHE_KEY, "");
        String labelWidth = uncertain.composite.TextParser.parse(view.getString("labelwidth"), model);
        formConfig.put("labelwidth", "".equals(labelWidth) ? null : labelWidth);
        String href = view.getString(PROPERTITY_MODEL, "");
        String dto = view.getString(PROPERTITY_DTO, "");
        dto = uncertain.composite.TextParser.parse(dto, model);
        if (StringUtils.isNotBlank(href)) {
            href = uncertain.composite.TextParser.parse(href, model);
            BusinessModelService modelService = mFactory.getModelService(href);
            BusinessModel bm = modelService.getBusinessModel();
            leaf.bm.Field[] fields = bm.getFields();
            int fl = fields.length;
            for (int n = 0; n < fl; n++) {
                leaf.bm.Field field = fields[n];
                if (field.isForQuery()) {
                    TextFieldConfig textField = TextFieldConfig.getInstance(field.getObjectContext());
                    textField.setWidth(field.getQueryWidth());
                    if (!"".equals(target)) textField.setBindTarget(target);
                    if (handler != null) {
                        EventConfig ec = EventConfig.getInstance();
                        ec.setEventName("enterdown");
                        ec.setHandler(handler);
                        textField.addEvent(ec);
                    }
                    formConfig.addChild(textField.getObjectContext());
                }
            }
        } else if (StringUtils.isNotBlank(dto)) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(dto);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            for (java.lang.reflect.Field f : fields) {
                boolean annotationPresent = f.isAnnotationPresent(LovField.class);
                if (!annotationPresent) {
                    continue;
                }
                LovField annotation = f.getAnnotation(LovField.class);
                boolean forQuery = annotation.forQuery();
                if (forQuery) {
                    CompositeMap context = new CompositeMap("textField");
                    context.put("prompt", annotation.prompt());
                    context.put("displaywith", annotation.displayWidth());
                    context.put("databasetype", annotation.databaseType());
                    context.put("forquery", annotation.forQuery());
                    context.put("name", f.getName());
                    context.put("datatype", f.getType().getName());

                    if(f.getType().getName().equals("java.util.Date")){
                        DatePickerConfig dateFieldConfig = DatePickerConfig.getInstance(context);
                        dateFieldConfig.setWidth(annotation.queryWidth());
                        if (!"".equals(target))

                            dateFieldConfig.setBindTarget(target);
                        if (handler != null) {
                            EventConfig ec = EventConfig.getInstance();
                            ec.setEventName("enterdown");
                            ec.setHandler(handler);
                            dateFieldConfig.addEvent(ec);
                        }
                        formConfig.addChild(dateFieldConfig.getObjectContext());
                    }else {
                        TextFieldConfig textField = TextFieldConfig.getInstance(context);
                        textField.setWidth(annotation.queryWidth());
                        if (!"".equals(target))
                            textField.setBindTarget(target);
                        if (handler != null) {
                            EventConfig ec = EventConfig.getInstance();
                            ec.setEventName("enterdown");
                            ec.setHandler(handler);
                            textField.addEvent(ec);
                        }
                        formConfig.addChild(textField.getObjectContext());
                    }
                }
            }
        } else if (StringUtils.isNoneEmpty(lovCacheKey)) {
            List<DocLayoutConfigLov> lovs = docLayoutConfigCache.get(uncertain.composite.TextParser.parse(lovCacheKey, model));
            //按LovColSequence排序
            lovs.sort(new Comparator<DocLayoutConfigLov>() {
                @Override
                public int compare(DocLayoutConfigLov o1, DocLayoutConfigLov o2) {
                    return Integer.valueOf(String.valueOf(o1.getLovColSequence() - o2.getLovColSequence()));
                }
            });
            if (lovs != null) {
                for (DocLayoutConfigLov lov : lovs) {
                    boolean forQuery = "Y".endsWith(lov.getLovColForQuery());
                    if (forQuery) {
                        CompositeMap context = new CompositeMap("textField");
                        context.put("prompt", lov.getLovColPrompt());
                        context.put("displaywith", lov.getLovColDisplayWidth());
                        context.put("databasetype", lov.getLovDataType());
                        context.put("forquery", lov.getLovColForQuery());
                        context.put("name", lov.getLovColName());
                        context.put("datatype", lov.getLovDataType());


                        TextFieldConfig textField = TextFieldConfig.getInstance(context);
//                        todo: customize width
                        textField.setWidth(150);
                        if (!"".equals(target))
                            textField.setBindTarget(target);
                        if (handler != null) {
                            EventConfig ec = EventConfig.getInstance();
                            ec.setEventName("enterdown");
                            ec.setHandler(handler);
                            textField.addEvent(ec);
                        }
                        formConfig.addChild(textField.getObjectContext());
                    }
                }
            }
        }
        view.getParent().replaceChild(view, formConfig.getObjectContext());
        return EventModel.HANDLE_NORMAL;
    }

    public int attachTo(CompositeMap v, Configuration procConfig) {
        view = v;
        return IFeature.NORMAL;
    }
}
