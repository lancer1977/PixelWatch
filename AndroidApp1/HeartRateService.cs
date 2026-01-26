using Android;
using Android.Content;
using Android.Content.PM;
using Android.Hardware;
using Android.OS;
using Android.Runtime;
using AndroidX.Core.Content;
using Poly.WearOS.Permissions;
using Poly.WearOS.Services;

namespace Poly.WearOS;

public sealed class HeartRateService : Java.Lang.Object, IHeartRateService, ISensorEventListener
{
    public event EventHandler<int>? HeartRateChanged;

    SensorManager? _sensorManager;
    Sensor? _hrSensor;
    private Activity? _activity;
    public void Initialize(Activity activity)
    {
        _activity = activity;
    }

    public async Task<bool> EnsurePermissionAsync()
    { 
        if (ContextCompat.CheckSelfPermission(_activity, Manifest.Permission.BodySensors) == Permission.Granted)
            return true;

 
        bool result = await PermissionHelper.RequestPermissionAsync(_activity, Android.Manifest.Permission.BodySensors);
        return result;
    }

    public bool Start()
    { 
        _sensorManager = (SensorManager?)_activity.GetSystemService(Context.SensorService);
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
