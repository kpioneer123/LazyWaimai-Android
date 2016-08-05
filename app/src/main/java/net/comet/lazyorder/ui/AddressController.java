package net.comet.lazyorder.ui;

import android.text.TextUtils;
import com.google.common.base.Preconditions;
import net.comet.lazyorder.R;
import net.comet.lazyorder.model.bean.Address;
import net.comet.lazyorder.model.bean.ResponseError;
import net.comet.lazyorder.model.bean.User;
import net.comet.lazyorder.network.RestApiClient;
import net.comet.lazyorder.network.action.ErrorAction;
import net.comet.lazyorder.util.StringFetcher;
import java.util.List;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class AddressController extends BaseUiController<AddressController.AddressUi, AddressController.AddressUiCallbacks> {

    private final RestApiClient mRestApiClient;

    @Inject
    public AddressController(RestApiClient restApiClient) {
        super();
        mRestApiClient = Preconditions.checkNotNull(restApiClient, "trackClient cannot be null");
    }

    @Override
    protected String getUiTitle(AddressUi ui) {
        if (ui instanceof AddressListUi) {
            return StringFetcher.getString(R.string.title_my_address);
        } else if (ui instanceof UpdateAddressUi) {
            Address address = ((UpdateAddressUi) ui).getRequestParameter();
            if (address == null) {
                return StringFetcher.getString(R.string.title_create_address);
            } else {
                return StringFetcher.getString(R.string.title_change_address);
            }
        }
        return null;
    }

    @Override
    protected void populateUi(AddressUi ui) {
        if (ui instanceof AddressListUi) {
            fetchAddressList(getId(ui));
        }
    }

    private void fetchAddressList(final int callingId) {
        mRestApiClient.addressService()
                .addresses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Address>>() {
                    @Override
                    public void call(List<Address> addresses) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof AddressListUi) {
                            ((AddressListUi) ui).onChangeItem(addresses);
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof AddressListUi) {
                            ((AddressListUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    private void doCreateAddress(final int callingId, Address address) {
        mRestApiClient.addressService()
                .create(address.getName(), address.getPhone(), address.getSummary(), address.getDetail())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Address>() {
                    @Override
                    public void call(Address address) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof UpdateAddressUi) {
                            ((UpdateAddressUi) ui).updateFinish();
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof UpdateAddressUi) {
                            ((UpdateAddressUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    private void doChangeAddress(final int callingId, Address address) {
        mRestApiClient.addressService()
                .change(address.getId(), address.getName(), address.getPhone(), address.getSummary(), address.getDetail())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Address>() {
                    @Override
                    public void call(Address address) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof UpdateAddressUi) {
                            ((UpdateAddressUi) ui).updateFinish();
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof UpdateAddressUi) {
                            ((UpdateAddressUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    private void doDeleteAddress(final int callingId, final Address address) {
        mRestApiClient.addressService()
                .delete(address.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object object) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof AddressListUi) {
                            ((AddressListUi) ui).deleteFinish(address);
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof AddressListUi) {
                            ((AddressListUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    private void doSetDefaultAddress(final int callingId, final Address address) {
        mRestApiClient.accountService()
                .setLastAddress(address.getUserId(), address.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof AddressListUi) {
                            ((AddressListUi) ui).setDefaultFinish();
                        }
                    }
                }, new ErrorAction() {
                    @Override
                    public void call(ResponseError error) {
                        AddressUi ui = findUi(callingId);
                        if (ui instanceof AddressListUi) {
                            ((AddressListUi) ui).onNetworkError(error);
                        }
                    }
                });
    }

    @Override
    protected AddressUiCallbacks createUiCallbacks(final AddressUi ui) {
        return new AddressUiCallbacks() {

            @Override
            public void refresh() {
                if (ui instanceof AddressListUi) {
                    fetchAddressList(getId(ui));
                }
            }

            @Override
            public boolean isNameValid(String name) {
                return !TextUtils.isEmpty(name);
            }

            @Override
            public boolean isMobileValid(String phone) {
                return !TextUtils.isEmpty(phone);
            }

            @Override
            public boolean isSummaryValid(String summary) {
                return !TextUtils.isEmpty(summary);
            }

            @Override
            public boolean isDetailValid(String detail) {
                return !TextUtils.isEmpty(detail);
            }

            @Override
            public void create(Address address) {
                doCreateAddress(getId(ui), address);
            }

            @Override
            public void change(Address address) {
                doChangeAddress(getId(ui), address);
            }

            @Override
            public void delete(Address address) {
                doDeleteAddress(getId(ui), address);
            }

            @Override
            public void setDefaultAddress(Address address) {
                doSetDefaultAddress(getId(ui), address);
            }

            @Override
            public void showChangeAddress(Address address) {
                Display display = getDisplay();
                if (display != null) {
                    display.showChangeAddress(address);
                }
            }

            @Override
            public void showCreateAddress() {
                Display display = getDisplay();
                if (display != null) {
                    display.showCreateAddress();
                }
            }
        };
    }

    public interface AddressUiCallbacks {

        void refresh();

        boolean isNameValid(String name);

        boolean isMobileValid(String phone);

        boolean isSummaryValid(String summary);

        boolean isDetailValid(String detail);

        void delete(Address address);

        void create(Address address);

        void change(Address address);

        void setDefaultAddress(Address address);

        void showChangeAddress(Address address);

        void showCreateAddress();
    }

    /**
     * UI界面必须实现的接口,留给控制器调用
     */
    public interface AddressUi extends BaseUiController.Ui<AddressUiCallbacks> {}

    public interface AddressListUi extends AddressUi {
        void onNetworkError(ResponseError error);

        void onChangeItem(List<Address> items);

        void deleteFinish(Address address);

        void setDefaultFinish();
    }

    public interface UpdateAddressUi extends AddressUi {
        void onNetworkError(ResponseError error);

        void updateFinish();

        Address getRequestParameter();
    }
}
