using Android.App;
using Android.Runtime;

namespace WearHrMaui;

[Application]
public class MainApplication : MauiApplication
{
    public static IServiceProvider Services { get; private set; }
    public MainApplication(IntPtr handle, JniHandleOwnership ownerShip) : base(handle, ownerShip) { }
    protected override MauiApp CreateMauiApp()
    {
        
        var app =  MauiProgram.CreateMauiApp();

        Services = app.Services;

        return app;
    }
}
