package takephoto.permission;


import takephoto.model.InvokeParam;


public interface InvokeListener {
    PermissionManager.TPermissionType invoke(InvokeParam invokeParam);
}
