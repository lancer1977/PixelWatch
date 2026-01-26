using Android.App;
using Android.Content.PM;
using AndroidX.Core.App;
using AndroidX.Core.Content;

namespace WearHrMaui.Platforms.Android.Permissions
{
    public static class PermissionHelper
    {
        private static TaskCompletionSource<bool>? _tcs;

        public static Task<bool> RequestPermissionAsync(Activity activity, string permission, int requestCode = 1001)
        {
            if (ContextCompat.CheckSelfPermission(activity, permission) == Permission.Granted)
                return Task.FromResult(true);

            _tcs = new TaskCompletionSource<bool>();

            ActivityCompat.RequestPermissions(activity, new[] { permission }, requestCode);

            return _tcs.Task;
        }

        public static void OnRequestPermissionsResult(int requestCode, string[] permissions, Permission[] grantResults)
        {
            if (requestCode == 1001)
            {
                var granted = grantResults.Length > 0 && grantResults[0] == Permission.Granted;
                _tcs?.TrySetResult(granted);
            }
        }
    }
}
