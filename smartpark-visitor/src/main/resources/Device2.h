
#define WM_DecodeSucess     123
#define WM_ReleaseCamera    124
void GetAppHandle(HWND hParent);
void ReleaseLostDevice();
void ReleaseDevice();
int StartDevice();
int GetDevice();
void  SetLed(bool bctrLed);
void  SetBeep(bool bctrlBeep);
void SetBeepTime(int BeepTime);
void  setQRable(bool bqr);
void  setDMable(bool bdm);
void  setBarcode(bool bbarcode);
void  GetDecodeString(char *aa,int &len);

