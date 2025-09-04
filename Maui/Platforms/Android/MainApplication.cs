using Android.App;
using Android.Runtime;

namespace WearHrMaui;

[Application]
public class MainApplication : MauiApplication
{
    public MainApplication(IntPtr handle, JniHandleOwnership ownerShip) : base(handle, ownerShip) { }
    protected override MauiApp CreateMauiApp() => MauiProgram.CreateMauiApp();
}
