namespace Poly.WearOS.Services;

public interface IHeartRateService
{
    event EventHandler<int>? HeartRateChanged;

    Task<bool> EnsurePermissionAsync();
    bool Start();
    void Stop();
}
