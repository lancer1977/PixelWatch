using _Microsoft.Android.Resource.Designer;
using Android.Hardware;

namespace Poly.WearOS
{
    [Activity(Label = "@string/app_name", MainLauncher = true)]
    public class MainActivity : Activity
    { 
        private TextView _heartRateText = null!;
        private HeartRateService _heartRateService;
        private EventHandler<int> onChange;
        protected override void OnCreate(Bundle? savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(ResourceConstant.Layout.activity_main);

            _heartRateText = FindViewById<TextView>(Resource.Id.heartRateText)!;

            _heartRateService = new HeartRateService();
            _heartRateService.Initialize(this);

            _heartRateService.HeartRateChanged += (_, bpm) =>
            {
                RunOnUiThread(() =>
                {
                    _heartRateText.Text = $"{bpm} bpm";
                });
            };

            _heartRateService.Start();
        }

        protected override void OnDestroy()
        {
            _heartRateService?.Stop();
            base.OnDestroy();
        }
    }
}
