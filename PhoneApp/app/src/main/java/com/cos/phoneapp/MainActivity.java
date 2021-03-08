package com.cos.phoneapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    private RecyclerView rvPhone;
    private PhoneAdapter phoneAdapter;
    private FloatingActionButton fabSave;
    private TextInputEditText inName;
    private TextInputEditText inTel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        findAll();
        fabSave.setOnClickListener(v -> {
            // inflate ( 위치, 레이아웃, 뷰그룹)
            // 다이얼로그는 액티비티 위에 덮어 씌우서 그릴 것이기 떄문에 뷰 그룹이 null이.
            // 객체 생성
            View dialog = v.inflate(v.getContext(), R.layout.dialog_item, null);
            AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
            dlg.setTitle("추가하기");

            // 다이얼에 있는 인풋을 찾기위해선 다이얼에서 findViewById를 해줘야 한다
            // 잊어 먹을 수 있으니 기록
            dlg.setView(dialog);
            inName = dialog.findViewById(R.id.in_name);
            inTel = dialog.findViewById(R.id.in_tel);
            dlg.setPositiveButton("추가", (dialog1, which) -> {
                Phone phone = new Phone();
                phone.setName(inName.getText().toString());
                phone.setTel(inTel.getText().toString());
                save(phone);
                // UI동기화를 위해서 다시 전체 검색 (비효율적)
                findAll();
            });
            dlg.setNegativeButton("확인", (dialog1, which) -> {

            });
            dlg.show();
        });


    }

    private void findAll() {
        PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);

        Log.d(TAG, "onCreate: 전체검색 데이터 : " + phoneService.findAll());
        Call<CMRespDto<List<Phone>>> call = phoneService.findAll();
        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                Log.d(TAG, "onResponse: 전체검색 성공");
                CMRespDto<List<Phone>> cmRespDto = response.body();
                List<Phone> phones = cmRespDto.getData();
                // 어댑터에게 넘기기
                Log.d(TAG, "onResponse: 데이터는 : " + phones);
                phoneAdapter = new PhoneAdapter(phones, MainActivity.this);
                // 메소드 속 스택 공간안에서 어댑터를 설정해줘야지
                // 메인 액티비티에 어댑터가 적용되어 그려진다 꼭 명심 (기본적인것)
                rvPhone.setAdapter(phoneAdapter);
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "onResponse: 전체 검색 실패 : " + t.getMessage());
            }
        });
    }


    private void save(Phone phone) {
        PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
        Log.d(TAG, "save: " + phoneService.save(phone));
        // 서버에서 리턴해주는 것이 무엇인지 정확하게 해고 제너럴 타입을 적어주도록 한다.
        Call<CMRespDto<Phone>> saveCall = phoneService.save(phone);
        saveCall.enqueue(new Callback<CMRespDto<Phone>>() {
            @Override
            public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                Log.d(TAG, "onResponse: response data : " + response.body());
                CMRespDto<Phone> cmRespDto = response.body();
                Log.d(TAG, "onResponse: cmRespDto : " + cmRespDto.getData());

            }

            @Override
            public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                Log.d(TAG, "onResponse: 데이터 추가 실패 : " + t.getMessage());

            }
        });
    }

    private void init() {
        rvPhone = findViewById(R.id.rv_phone);
        fabSave = findViewById(R.id.fab_save);
        LinearLayoutManager manager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvPhone.setLayoutManager(manager);
    }
}