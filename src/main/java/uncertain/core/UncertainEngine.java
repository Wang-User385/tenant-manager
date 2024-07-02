//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package uncertain.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.springframework.util.ResourceUtils;
import uncertain.composite.CompositeLoader;
import uncertain.composite.CompositeMap;
import uncertain.event.Configuration;
import uncertain.event.IContextListener;
import uncertain.event.IEventDispatcher;
import uncertain.event.RuntimeContext;
import uncertain.exception.BuiltinExceptionFactory;
import uncertain.exception.IExceptionListener;
import uncertain.logging.BasicConsoleHandler;
import uncertain.logging.BasicFileHandler;
import uncertain.logging.DummyLogger;
import uncertain.logging.ILogPathSettable;
import uncertain.logging.ILogger;
import uncertain.logging.ILoggerProvider;
import uncertain.logging.ILoggerProviderGroup;
import uncertain.logging.ILoggingTopicRegistry;
import uncertain.logging.LoggerProvider;
import uncertain.logging.LoggingTopic;
import uncertain.logging.TopicManager;
import uncertain.mbean.IMBeanNameProvider;
import uncertain.mbean.IMBeanRegister;
import uncertain.mbean.IMBeanRegistrable;
import uncertain.mbean.MBeanRegister;
import uncertain.mbean.UncertainEngineWrapper;
import uncertain.ocm.ClassRegistry;
import uncertain.ocm.IClassLocator;
import uncertain.ocm.IObjectCreator;
import uncertain.ocm.IObjectRegistry;
import uncertain.ocm.OCManager;
import uncertain.ocm.ObjectRegistryImpl;
import uncertain.pkg.IInstanceCreationListener;
import uncertain.pkg.IPackageManager;
import uncertain.pkg.PackageManager;
import uncertain.pkg.PackagePath;
import uncertain.proc.IProcedureManager;
import uncertain.proc.ParticipantRegistry;
import uncertain.proc.Procedure;
import uncertain.proc.ProcedureManager;
import uncertain.proc.ProcedureRunner;
import uncertain.schema.ISchemaManager;
import uncertain.schema.SchemaManager;
import uncertain.util.resource.ILocatable;
import uncertain.util.resource.ISourceFileManager;
import uncertain.util.resource.SourceFileManager;

public class UncertainEngine implements IContainer, IMBeanNameProvider {
    public static final String DEFAULT_CONFIG_FILE_PATTERN = ".*\\.config";
    public static final String DEFAULT_ENGINE_NAME = "uncertain_engine";
    public static final String UNCERTAIN_LOGGING_TOPIC = "uncertain.core";
    CompositeLoader mCompositeLoader = CompositeLoader.createInstanceForOCM();
    OCManager mOcManager;
    ObjectRegistryImpl mObjectRegistry;
    ClassRegistry mClassRegistry;
    ParticipantRegistry mParticipantRegistry;
    Configuration mConfig;
    CompositeMap mGlobalContext;
    DirectoryConfig mDirectoryConfig;
    ProcedureManager mProcedureManager;
    SourceFileManager mSourceFileManager;
    SchemaManager mSchemaManager;
    PackageManager mPackageManager;
    String mName = "uncertain_engine";
    Set mContextListenerSet;
    File mConfigDir;
    boolean mIsRunning = true;
    Set<String> mLoadedFiles = new HashSet();
    List<ILifeCycle> mLoadedLifeCycleList = new LinkedList();
    String mDefaultLogLevel = "WARNING";
    ILogger mLogger;
    TopicManager mTopicManager;
    Throwable mInitException;
    boolean mContinueLoadInstanceWithException = false;
    private String configDir;

    public void setConfigDir(String configDir) {
        this.configDir = configDir;
    }

    public static UncertainEngine createInstance() {
        UncertainEngine engine = new UncertainEngine();
        engine.initialize((CompositeMap)null);
        return engine;
    }

    public UncertainEngine(CompositeMap config) {
        this.bootstrap();
        this.initialize(config);
    }

    public UncertainEngine() {
        this.bootstrap();
    }

    public UncertainEngine(File configDir, String configFileName) {
        this.bootstrap();
        this.setConfigDirectory(configDir);
        CompositeMap configMap = null;

        try {
            configMap = this.mCompositeLoader.loadByFile((new File(configDir, configFileName)).getAbsolutePath());
        } catch (Exception var5) {
            throw new RuntimeException("Error when loading configuration file " + configFileName, var5);
        }

        this.initialize(configMap);
    }

    public UncertainEngine(String configDir, String configFileName) {
        this.bootstrap();
        this.mDirectoryConfig.setConfigDirectory(configDir);
        CompositeMap configMap = null;

        try {
            URL url = new URL(ResourceUtils.getURL(configDir), configFileName);
            configMap = this.mCompositeLoader.loadFromStream(url.openStream());
        } catch (Exception var5) {
            throw new RuntimeException("Error when loading configuration file " + configFileName, var5);
        }

        this.initialize(configMap);
    }

    private void setInitError(Throwable thr) {
        this.mIsRunning = false;
        this.mInitException = thr;
    }

    private void registerBuiltinInstances() {
        this.mObjectRegistry.registerInstanceOnce(IContainer.class, this);
        this.mObjectRegistry.registerInstanceOnce(UncertainEngine.class, this);
        this.mObjectRegistry.registerInstance(CompositeLoader.class, this.mCompositeLoader);
        this.mObjectRegistry.registerInstance(IClassLocator.class, this.mClassRegistry);
        this.mObjectRegistry.registerInstance(ClassRegistry.class, this.mClassRegistry);
        this.mObjectRegistry.registerInstance(ParticipantRegistry.class, this.mParticipantRegistry);
        this.mObjectRegistry.registerInstance(OCManager.class, this.mOcManager);
        this.mObjectRegistry.registerInstance(DirectoryConfig.class, this.mDirectoryConfig);
        this.mObjectRegistry.registerInstanceOnce(IObjectRegistry.class, this.mObjectRegistry);
        this.mObjectRegistry.registerInstanceOnce(IObjectCreator.class, this.mObjectRegistry);
        this.mObjectRegistry.registerInstanceOnce(ILoggingTopicRegistry.class, this.mTopicManager);
        this.mObjectRegistry.registerInstanceOnce(ILogger.class, this.mLogger);
        this.mObjectRegistry.registerInstanceOnce(IProcedureManager.class, this.getProcedureManager());
        this.mObjectRegistry.registerInstanceOnce(ISourceFileManager.class, this.mSourceFileManager);
        this.mObjectRegistry.registerInstance(IPackageManager.class, this.mPackageManager);
        this.mObjectRegistry.registerInstanceOnce(ISchemaManager.class, this.mSchemaManager);
    }

    private void loadBuiltinPackages() throws IOException {
        this.mPackageManager.loadPackageFromRootClassPath("uncertain_builtin_package");
    }

    private void setDefaultClassRegistry() {
        this.mClassRegistry.registerPackage("uncertain.proc");
        this.mClassRegistry.registerPackage("uncertain.ocm");
        this.mClassRegistry.registerPackage("uncertain.logging");
        this.mClassRegistry.registerPackage("uncertain.core");
        this.mClassRegistry.registerPackage("uncertain.core.admin");
        this.mClassRegistry.registerPackage("uncertain.event");
        this.mClassRegistry.registerPackage("uncertain.pkg");
        this.mClassRegistry.registerPackage("uncertain.cache");
        this.mClassRegistry.registerPackage("uncertain.cache.action");
        this.mClassRegistry.registerClass("class-registry", "uncertain.ocm", "ClassRegistry");
        this.mClassRegistry.registerClass("package-mapping", "uncertain.ocm", "PackageMapping");
        this.mClassRegistry.registerClass("class-mapping", "uncertain.ocm", "ClassMapping");
        this.mClassRegistry.registerClass("feature-attach", "uncertain.ocm", "FeatureAttach");
        this.mClassRegistry.registerClass("package-path", "uncertain.pkg", "PackagePath");
        this.loadInternalRegistry("uncertain.logging.DefaultRegistry");
    }

    private void loadBuiltinLoggingTopic() {
        this.mTopicManager.registerLoggingTopic("uncertain.core");
        this.mTopicManager.registerLoggingTopic("uncertain.ocm");
        this.mTopicManager.registerLoggingTopic("uncertain.event");
        this.mTopicManager.registerLoggingTopic("uncertain.proc");
    }

    private void loadInternalRegistry(String file_path) {
        CompositeMap map = this.loadCompositeMap(file_path);
        if (map == null) {
            throw new RuntimeException("Can't load internal resource " + file_path);
        } else {
            ClassRegistry reg = (ClassRegistry)this.mOcManager.createObject(map);
            this.mClassRegistry.addAll(reg);
        }
    }

    protected void bootstrap() {
        this.mCompositeLoader = CompositeLoader.createInstanceForOCM();
        this.mDirectoryConfig = DirectoryConfig.createDirectoryConfig();
        this.mContextListenerSet = new HashSet();
        this.mObjectRegistry = new ObjectRegistryImpl();
        this.mOcManager = new OCManager(this.mObjectRegistry);
        this.mProcedureManager = new ProcedureManager(this);
        this.mClassRegistry = this.mOcManager.getClassRegistry();
        this.setDefaultClassRegistry();
        this.mParticipantRegistry = new ParticipantRegistry();
        this.mGlobalContext = new CompositeMap("global");
        this.mTopicManager = new TopicManager();
        this.mSourceFileManager = SourceFileManager.getInstance();
        this.mSourceFileManager.startup();
        this.mSchemaManager = new SchemaManager();
        this.mSchemaManager.addSchema(SchemaManager.getSchemaForSchema());
        this.mPackageManager = new PackageManager(this.mCompositeLoader, this.mOcManager, this.mSchemaManager);
        this.registerBuiltinInstances();

        try {
            this.loadBuiltinPackages();
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }

        this.loadBuiltinLoggingTopic();
    }

    public void initialize(CompositeMap config) {
        if (config != null) {
            this.mOcManager.populateObject(config, this);
            CompositeMap child = config.getChild("path-config");
            if (child != null) {
                if (this.mDirectoryConfig == null) {
                    this.mDirectoryConfig = DirectoryConfig.createDirectoryConfig(child);
                } else {
                    this.mDirectoryConfig.merge(child);
                }
            }

            this.mDirectoryConfig.checkValidation();
        }

    }

    protected ILoggerProvider createDefaultLoggerProvider() {
        LoggerProvider clp = new LoggerProvider();
        clp.setDefaultLogLevel(this.mDefaultLogLevel);
        clp.addTopics(new LoggingTopic[]{new LoggingTopic("uncertain.core", Level.INFO), new LoggingTopic("uncertain.ocm", Level.WARNING)});
        String log_path = this.mDirectoryConfig.getLogDirectory();
        if (log_path == null) {
            clp.addHandle(new BasicConsoleHandler());
        } else {
            BasicFileHandler fh = new BasicFileHandler();
            fh.setLogPath(log_path);
            fh.setLogFilePrefix(this.getName());
            clp.addHandle(fh);
        }

        return clp;
    }

    protected void checkLogger() {
        ILoggerProvider logger_provider = (ILoggerProvider)this.mObjectRegistry.getInstanceOfType(ILoggerProvider.class);
        if (logger_provider == null) {
            logger_provider = this.createDefaultLoggerProvider();
            this.mObjectRegistry.registerInstance(ILoggerProvider.class, logger_provider);
            this.mOcManager.setLoggerProvider(logger_provider);
        }

        this.mLogger = logger_provider.getLogger("uncertain.core");
    }

    public void addLoggingConfig(ILoggerProvider logging_config) {
        if (logging_config instanceof ILoggerProviderGroup) {
            ILoggerProviderGroup group = (ILoggerProviderGroup)logging_config;
            group.registerTo(this.mObjectRegistry);
        }

        if (logging_config instanceof IContextListener) {
            this.mContextListenerSet.add(logging_config);
        }

        if (logging_config instanceof ILogPathSettable) {
            ILogPathSettable lp = (ILogPathSettable)logging_config;
            String log_path = this.mDirectoryConfig.getLogDirectory();
            if (log_path != null && lp.getLogPath() == null) {
                lp.setLogPath(log_path);
            }
        }

    }

    public ILogger getLogger(String topic) {
        ILoggerProvider provider = (ILoggerProvider)this.mObjectRegistry.getInstanceOfType(ILoggerProvider.class);
        return provider == null ? DummyLogger.getInstance() : provider.getLogger(topic);
    }

    public void logException(String message, Throwable thr) {
        this.mLogger.log(Level.SEVERE, message, thr);
        IExceptionListener exceptionListener = (IExceptionListener)this.mObjectRegistry.getInstanceOfType(IExceptionListener.class);
        if (exceptionListener != null) {
            exceptionListener.onException(thr);
        }

    }

    private boolean runInitProcedure(Procedure proc) {
        return this.runInitProcedure(proc, (CompositeMap)null);
    }

    private boolean runInitProcedure(Procedure proc, CompositeMap context) {
        ProcedureRunner runner = this.createProcedureRunner(proc);
        if (context != null) {
            runner.setContext(context);
        }

        runner.addConfiguration(this.mConfig);
        runner.run();
        Throwable thr = runner.getException();
        if (thr != null) {
            this.mLogger.log(Level.SEVERE, "An error happened during initialize process");
            this.logException("Error when running procedure " + proc.getOriginSource() == null ? "" : proc.getOriginSource(), thr);
            this.setInitError(thr);
            return false;
        } else {
            return true;
        }
    }

    private boolean loadInstance(Object inst) {
        this.mConfig.addParticipant(inst);
        if (inst instanceof IContextListener) {
            this.addContextListener((IContextListener)inst);
        }

        if (inst instanceof ILifeCycle) {
            ILifeCycle c = (ILifeCycle)inst;
            if (c.startup()) {
                this.mLoadedLifeCycleList.add(c);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void doConfigure(Collection cfg) {
        this.mConfig.loadConfigList(cfg);
        Iterator it = this.mConfig.getParticipantList().iterator();

        while(it.hasNext()) {
            Object inst = it.next();
            if (inst instanceof IGlobalInstance) {
                this.getObjectRegistry().registerInstance(inst);
            }

            if (inst instanceof IContextListener) {
                this.addContextListener((IContextListener)inst);
            }

            if (inst instanceof ILifeCycle) {
                ILifeCycle c = (ILifeCycle)inst;
                if (c.startup()) {
                    this.mLoadedLifeCycleList.add(c);
                }
            }
        }

    }

    private void runStartupProcedure() {
        Procedure proc = this.loadProcedure("uncertain.core.EngineInit");
        if (proc == null) {
            throw new IllegalArgumentException("Can't load uncertain/core/EngineInit from class loader");
        } else if (this.runInitProcedure(proc)) {
            ;
        }
    }

    public void loadConfigFile(String full_path) {
        try {
            CompositeMap data = this.mCompositeLoader.loadByFullFilePath(full_path);
            ArrayList lst = new ArrayList(1);
            lst.add(data);
            this.doConfigure(lst);
        } catch (Exception var4) {
            throw new RuntimeException("Can't load file " + full_path, var4);
        }
    }

    public Configuration createConfig() {
        Configuration conf = new Configuration(this.mParticipantRegistry, this.mOcManager);
        return conf;
    }

    public Configuration createConfig(CompositeMap cfg) {
        Configuration conf = new Configuration(this.mParticipantRegistry, this.mOcManager);
        conf.loadConfig(cfg);
        return conf;
    }

    private CompositeMap loadCompositeMap(String class_path) {
        try {
            CompositeMap m = this.mCompositeLoader.loadFromClassPath(class_path);
            return m;
        } catch (Exception var3) {
            throw new RuntimeException("Can't load CompositeMap from path " + class_path, var3);
        }
    }

    private Procedure loadProcedure(String class_path) {
        try {
            return this.mProcedureManager.loadProcedure(class_path);
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    private ProcedureRunner createProcedureRunner(Procedure proc) {
        if (proc == null) {
            return null;
        } else {
            ProcedureRunner runner = new ProcedureRunner();
            runner.setContext(this.mGlobalContext);
            runner.setUncertainEngine(this);
            runner.setProcedure(proc);
            runner.setLogger(this.mLogger);
            return runner;
        }
    }

    public void initContext(CompositeMap context) {
        Iterator it = this.mContextListenerSet.iterator();

        while(it.hasNext()) {
            IContextListener listener = (IContextListener)it.next();
            listener.onContextCreate(RuntimeContext.getInstance(context));
        }

    }

    public void destroyContext(CompositeMap context) {
        Iterator it = this.mContextListenerSet.iterator();

        while(it.hasNext()) {
            IContextListener listener = (IContextListener)it.next();
            listener.onContextDestroy(RuntimeContext.getInstance(context));
        }

    }

    public void addPackages(PackagePath[] paths) throws IOException {
        for(int i = 0; i < paths.length; ++i) {
            this.mPackageManager.loadPackage(paths[i]);
        }

    }

    public Throwable getInitializeException() {
        return this.mInitException;
    }

    public boolean isRunning() {
        return this.mIsRunning;
    }

    public void setConfigDirectory(File dir) {
        this.mConfigDir = dir;
        this.mDirectoryConfig.setConfigDirectory(dir.getPath());
    }

    public void addClassRegistry(ClassRegistry reg) {
        this.mClassRegistry.addAll(reg);
    }

    public void addClassRegistry(ClassRegistry reg, boolean override) {
        this.mClassRegistry.addAll(reg, override);
    }

    public ClassRegistry getClassRegistry() {
        return this.mClassRegistry;
    }

    public CompositeLoader getCompositeLoader() {
        return this.mCompositeLoader;
    }

    public OCManager getOcManager() {
        return this.mOcManager;
    }

    public IObjectCreator getObjectCreator() {
        return this.mObjectRegistry;
    }

    public IObjectRegistry getObjectRegistry() {
        return this.mObjectRegistry;
    }

    public IEventDispatcher getEventDispatcher() {
        return this.mConfig;
    }

    public ParticipantRegistry getParticipantRegistry() {
        return this.mParticipantRegistry;
    }

    public CompositeMap getGlobalContext() {
        return this.mGlobalContext;
    }

    public File getConfigDirectory() {
        if (this.mConfigDir == null) {
            String dir = this.mDirectoryConfig.getConfigDirectory();
            if (dir != null) {
                this.mConfigDir = new File(dir);
            }
        }

        return this.mConfigDir;
    }

    public boolean getIsRunning() {
        return this.mIsRunning;
    }

    public void addContextListener(IContextListener listener) {
        this.mContextListenerSet.add(listener);
    }

    public void startup() {
        this.startup(true);
    }

    private void loadInstanceFromPackage() {
        this.mPackageManager.createInstances(this.mObjectRegistry, new IInstanceCreationListener() {
            public void onInstanceCreate(Object instance, File config_file) {
                if (!UncertainEngine.this.loadInstance(instance)) {
                    throw BuiltinExceptionFactory.createInstanceStartError(instance, config_file.getAbsolutePath(), (Throwable)null);
                } else {
                    UncertainEngine.this.mLoadedFiles.add(config_file.getAbsolutePath());
                    UncertainEngine.this.mLogger.info("Loaded instance " + instance.getClass().getName() + " from " + config_file.getAbsolutePath());
                }
            }
        }, this.mContinueLoadInstanceWithException);
    }

    public void startup(boolean scan_config_files) {
        long tick = System.currentTimeMillis();
        this.mIsRunning = false;
        this.mConfig = this.createConfig();
        this.mConfig.setLogger(this.mLogger);
        File local_config_file = new File(this.getConfigDirectory(), "uncertain.local.xml");
        CompositeMap local_config_map = null;
        if (local_config_file.exists()) {
            try {
                local_config_map = this.mCompositeLoader.loadByFile(local_config_file.getAbsolutePath());
            } catch (Exception var7) {
                throw new RuntimeException(var7);
            }

            this.initialize(local_config_map);
        } else {
            this.autoConfig();
        }

        this.checkLogger();
        this.mLogger.log("Uncertain engine startup");
        if (scan_config_files) {
            this.loadInstanceFromPackage();
        }

        this.runStartupProcedure();
        this.mIsRunning = true;
        this.registerMBean();
        tick = System.currentTimeMillis() - tick;
        this.mLogger.info("UncertainEngine startup success in " + tick + " ms");
    }

    private void autoConfig() {
        CompositeMap map = this.mDirectoryConfig.getObjectContext();
        String classPath = this.getClass().getClassLoader().getResource("").getPath();
        int index = classPath.indexOf("!");
        File classFile;
        if (index <= 0) {
            classFile = new File(classPath);
        } else {
            String tempPath = this.mPackageManager.getTempPath();
            File tempDir;
            if (tempPath != null) {
                tempDir = new File(tempPath);
                if (!tempDir.exists() || !tempDir.isDirectory()) {
                    throw BuiltinExceptionFactory.createInvalidPathException((ILocatable)null, tempPath);
                }
            } else {
                tempDir = new File(System.getProperty("java.io.tmpdir"));
            }

            classFile = tempDir;
        }
        File logs = new File("/u01/logs");
        if(!logs.exists()){
            logs = new File(classFile, "logs");
            if (!logs.exists()) {
                logs.mkdirs();
            }
        }
        map.put("logpath", logs.getAbsolutePath());
        String subPath = "lib/LeafUI/src";
        map.put("uiPackageBasePath", (new File(classFile, subPath)).getAbsolutePath());
    }

    public void shutdown() {
        if (this.mSourceFileManager != null) {
            this.mSourceFileManager.shutdown();
        }

        if (this.mLogger != null) {
            this.mLogger.log("Uncertain engine shutdown");
        }

        Procedure proc = this.loadProcedure("uncertain.core.EngineShutdown");
        if (proc == null) {
            throw new IllegalArgumentException("Can't load uncertain/core/EngineShutdown.proc from class loader");
        } else {
            ProcedureRunner runner = this.createProcedureRunner(proc);
            if (this.mConfig != null) {
                runner.addConfiguration(this.mConfig);
            }

            runner.run();
            Iterator var3 = this.mLoadedLifeCycleList.iterator();

            while(var3.hasNext()) {
                ILifeCycle l = (ILifeCycle)var3.next();

                try {
                    l.shutdown();
                } catch (Throwable var6) {
                    this.mLogger.log(Level.WARNING, "Error when shuting down instance " + l, var6);
                }
            }

            this.mIsRunning = false;
        }
    }

    public DirectoryConfig getDirectoryConfig() {
        return this.mDirectoryConfig;
    }

    public IProcedureManager getProcedureManager() {
        return this.mProcedureManager;
    }

    public String getName() {
        return this.mName;
    }

    public String getMBeanName(String category, String sub_name) {
        String name = "org.uncertain:instance=" + this.getName();
        if (category != null) {
            name = name + ",category=" + category;
        }

        if (sub_name != null) {
            name = name + "," + sub_name;
        }

        return name;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDefaultLogLevel() {
        return this.mDefaultLogLevel;
    }

    public void setDefaultLogLevel(String defaultLogLevel) {
        Level.parse(defaultLogLevel);
        this.mDefaultLogLevel = defaultLogLevel;
    }

    public PackageManager getPackageManager() {
        return this.mPackageManager;
    }

    public ISchemaManager getSchemaManager() {
        return this.mSchemaManager;
    }

    public void registerMBean() {
        IMBeanRegister register = MBeanRegister.getInstance();
        String name = this.getMBeanName((String)null, "name=Engine");
        UncertainEngineWrapper wrapper = new UncertainEngineWrapper(this);

        try {
            register.register(name, wrapper);
        } catch (Exception var10) {
            this.mLogger.log(Level.WARNING, "Can't register MBean for UncertainEngine", var10);
        }

        Set reg_set = new HashSet();
        Iterator it = this.mObjectRegistry.getInstanceMapping().values().iterator();

        while(it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof IMBeanRegistrable && !reg_set.contains(obj)) {
                IMBeanRegistrable reg = (IMBeanRegistrable)obj;

                try {
                    reg.registerMBean(register, this);
                    reg_set.add(obj);
                } catch (Exception var9) {
                    this.mLogger.log(Level.WARNING, "Can't register MBean " + obj, var9);
                }
            }
        }

        reg_set.clear();
    }

    public boolean isContinueLoadConfigWithException() {
        return this.mContinueLoadInstanceWithException;
    }

    public void setContinueLoadConfigWithException(boolean continueLoadConfigWithException) {
        this.mContinueLoadInstanceWithException = continueLoadConfigWithException;
    }
}
