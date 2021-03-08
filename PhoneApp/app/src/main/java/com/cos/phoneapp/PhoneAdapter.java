package com.cos.phoneapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 어댑터와 리사이클러뷰와 연결 (데이터 바인딩, MVVM 사용 안해도됨)
public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.MyViewHolder> {

    private static final String TAG = "PhoneAdapter";
    private final List<Phone> phones;
    private final Context mContext;

    public PhoneAdapter(List<Phone> phones, Context mContext) {
        this.phones = phones;
        this.mContext = mContext;
    }
//    추가하기에 대해선 더 고민해보자.
//    public void addItem(Phone phone){
//        phones.add(phone);
//        notifyDataSetChanged();
//    }

    public void removeItem(int pos){
        phones.remove(pos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.phone_item, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.setItem(phones.get(position));
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvTel;
        private TextInputEditText inName, inTel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvTel = itemView.findViewById(R.id.tv_tel);


            tvName.setOnClickListener(v -> {
                // inflate ( 위치, 레이아웃, 뷰그룹)
                // 다이얼로그는 액티비티 위에 덮어 씌우서 그릴 것이기 떄문에 뷰 그룹이 null이.
                // 객체 생성
                View dialog = v.inflate(v.getContext(), R.layout.dialog_item, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
                dlg.setTitle("수정하기");
                dlg.setView(dialog);

                // 다이얼에 있는 위젯을 다이얼에서 찾아줘야 한다.
                inName = dialog.findViewById(R.id.in_name);
                inTel = dialog.findViewById(R.id.in_tel);
                int pos = getAdapterPosition(); // 리스트속 아이템의 번호를 반환
                // 리스트 번호로 삭제를 하면 0번의 id는 존재하지 않기에 오류가 발생한다.
                // 그래서 리스트 번호를 통해 해당 아이템의 id를 찾아서 삭제한다.
                Phone phone = phones.get(pos);
                long id = phone.getId();

                PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
                Call<CMRespDto<Phone>> findByIdCall =  phoneService.findById(id);
                findByIdCall.enqueue(new Callback<CMRespDto<Phone>>() {
                    @Override
                    public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                        Log.d(TAG, "onResponse: 한건 찾기 성공 : " + response.body().getData());
                        CMRespDto<Phone> phoneCMRespDto = response.body();
                        Phone phone = phoneCMRespDto.getData();
                        inName.setText(phone.getName());
                        inTel.setText(phone.getTel());
                    }

                    @Override
                    public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                        Log.d(TAG, "onFailure: 한건 찾기 실패 : " + t.getMessage());
                    }
                });

                dlg.setPositiveButton("삭제", (dialog1, which) -> {

                    Log.d(TAG, "MyViewHolder: id : " + id);
                    Call<Void> deleteCall = phoneService.delete(id);
                    Log.d(TAG, "MyViewHolder: daleteCall : " + deleteCall);
                      deleteCall.enqueue(new Callback<Void>() {
                          @Override
                          public void onResponse(Call<Void> call, Response<Void> response) {
                              Log.d(TAG, "onResponse: 삭제 성공 : " + response.body());
                              removeItem(pos);
                          }

                          @Override
                          public void onFailure(Call<Void> call, Throwable t) {
                              Log.d(TAG, "onFailure: 삭제 실패 : " + t.getMessage());
                          }
                      });

                });
                dlg.setNegativeButton("수정", (dialog1, which) -> {
                    phone.setName(inName.getText().toString());
                    phone.setTel(inTel.getText().toString());

                    Call<CMRespDto<Phone>> updateCall = phoneService.update(phone, id);
                    updateCall.enqueue(new Callback<CMRespDto<Phone>>() {
                        @Override
                        public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                            Log.d(TAG, "onResponse: 수정 성공 : ");
                            // 수정 후 바로 UI를 동기화 해주면 된다.
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                            Log.d(TAG, "onFailure: 수정 실패 : " + t.getMessage());

                        }
                    });
                });
                dlg.show();
            });

        }

        public void setItem(Phone phone) {
            tvName.setText(phone.getName());
            tvTel.setText(phone.getTel());
        }
    }
}
