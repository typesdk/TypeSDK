package @package@.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.vivo.unionsdk.open.VivoPayUtils;

/**
 * Created by Huyancheng on 2016/8/9.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXPayEntryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VivoPayUtils.weiXinPayInit(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onReq(BaseReq mBaseReq) {
    }

    @Override
    public void onResp(BaseResp mBaseResp) {
        VivoPayUtils.weiXinPayResponse(this, mBaseResp);
    }
}
