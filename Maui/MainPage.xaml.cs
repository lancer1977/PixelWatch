using WearHrMaui.Services;

namespace WearHrMaui;

public partial class MainPage : ContentPage
{
    readonly IHeartRateService _hrService;

    public MainPage()
    {
        InitializeComponent();
        _hrService = Application.Current!.Services.GetRequiredService<IHeartRateService>();
        Loaded += OnLoaded;
        Unloaded += OnUnloaded;
    }

    private async void OnLoaded(object? sender, EventArgs e)
    {
        StatusLabel.Text = "Requesting permission...";
        var ok = await _hrService.EnsurePermissionAsync();
        if (!ok)
        {
            StatusLabel.Text = "BODY_SENSORS denied.";
            return;
        }

        StatusLabel.Text = "Starting sensor...";
        _hrService.HeartRateChanged += OnHeartRateChanged;
        var started = _hrService.Start();
        StatusLabel.Text = started ? "Live" : "No HR sensor";
    }

    private void OnUnloaded(object? sender, EventArgs e)
    {
        _hrService.HeartRateChanged -= OnHeartRateChanged;
        _hrService.Stop();
    }

    void OnHeartRateChanged(object? sender, int bpm)
    {
        MainThread.BeginInvokeOnMainThread(() =>
        {
            HrLabel.Text = $"{bpm}";
        });
    }
}
