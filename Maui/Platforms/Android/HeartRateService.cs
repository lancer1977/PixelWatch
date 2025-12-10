using Android;
using Android.Content;
using Android.Content.PM;
using Android.Hardware;
using Android.OS;
using Android.Runtime;
using AndroidX.Core.App;
using AndroidX.Core.Content;
using WearHrMaui.Services;

namespace WearHrMaui.Services;

public sealed class HeartRateService : Java.Lang.Object, IHeartRateService, ISensorEventListener
{
    public event EventHandler<int>? HeartRateChanged;

    SensorManager? _sensorManager;
    Sensor? _hrSensor;

    public async Task<bool> EnsurePermissionAsync()
    {
        var activity = Platform.CurrentActivity!;
        if (ContextCompat.CheckSelfPermission(activity, Manifest.Permission.BodySensors) == Permission.Granted)
            return true;

        var tcs = new TaskCompletionSource<bool>();

        ActivityCompat.RequestPermissions(activity, new[] { Manifest.Permission.BodySensors }, 1001);

        void Handler(object? s, Microsoft.Maui.ApplicationModel.PermissionsRequestEventArgs e)
        {
            if (e.RequestCode == 1001)
            {
                var granted = e.GrantResults.Length > 0 && e.GrantResults[0] == Permission.Granted;
                tcs.TrySetResult(granted);
                Platform.PermissionRequestCompleted -= Handler;
            }
        }

        Platform.PermissionRequestCompleted += Handler;
        return await tcs.Task.ConfigureAwait(false);
    }

    public bool Start()
    {
        var activity = Platform.CurrentActivity!;
        _sensorManager = (SensorManager?)activity.GetSystemService(Context.SensorService);
        _hrSensor = _sensorManager?.GetDefaultSensor(SensorType.HeartRate);

        if (_sensorManager is null || _hrSensor is null)
            return false;

        if (Build.VERSION.SdkInt >= BuildVersionCodes.Lollipop)
            _sensorManager.RegisterListener(this, _hrSensor, SensorDelay.Game);
        else
            _sensorManager.RegisterListener(this, _hrSensor, SensorDelay.Ui);

        return true;
    }

    public void Stop()
    {
        if (_sensorManager != null && _hrSensor != null)
            _sensorManager.UnregisterListener(this, _hrSensor);
    }

    public void OnAccuracyChanged(Sensor? sensor, [GeneratedEnum] SensorStatus accuracy) { }

    public void OnSensorChanged(SensorEvent? e)
    {
        if (e?.Sensor?.Type != SensorType.HeartRate) return;
        if (e.Values.Any())
        {
            var value = (int)Math.Max(0, e.Values[0]);
            HeartRateChanged?.Invoke(this, value);
        }
    }
}
