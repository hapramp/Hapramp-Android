package com.hapramp.views.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hapramp.R;
import com.hapramp.editor.Editor;
import com.hapramp.editor.EditorListener;
import com.hapramp.editor.models.EditorTextStyle;
import com.hapramp.models.PostJobModel;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Ankit on 12/31/2017.
 */

public class EditorView extends FrameLayout implements TextHeaderView.HeadingChangeListener, BoldButtonView.BoldTextListener, ItalicView.ItalicTextListener, BulletsView.BulletListener, LinkView.LinkInsertListener, DividerView.DividerListener, ImageInsertView.ImageInsertListener {


    @BindView(R.id.editor)
    Editor editor;
    @BindView(R.id.editorControlHolder)
    LinearLayout editorControlHolder;
    @BindView(R.id.textHeaderView)
    TextHeaderView textHeaderView;
    @BindView(R.id.bold_text_control)
    BoldButtonView boldTextControl;
    @BindView(R.id.italic_text_control)
    ItalicView italicTextControl;
    @BindView(R.id.bullets_control)
    BulletsView bulletsControl;
    @BindView(R.id.link_view)
    LinkView linkView;
    @BindView(R.id.paragraph_divider_view)
    DividerView paragraphDividerView;
    @BindView(R.id.image_insertBtn)
    ImageInsertView imageInsertBtn;
    private Context mContext;
    private View view;

    public EditorView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public EditorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.editor_view, this);
        ButterKnife.bind(this, view);
        attachListeners();
        editor.setEditorImageLayout(R.layout.article_image_template);
        editor.setListItemLayout(R.layout.bullet_view_layout);
        editor.setDividerLayout(R.layout.article_line_divider);
        editor.render();
    }

    private void attachListeners() {
        textHeaderView.setHeadingChangeListener(this);
        boldTextControl.setBoldTextListener(this);
        italicTextControl.setItalicTextListener(this);
        bulletsControl.setBulletListener(this);
        linkView.setLinkInsertListener(this);
        paragraphDividerView.setDividerListener(this);
        imageInsertBtn.setInsertListener(this);

        editor.setEditorListener(new EditorListener() {
            @Override
            public void onTextChanged(EditText editText, Editable text) {
                // Toast.makeText(EditorTestActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpload(Bitmap image, String uuid) {

                uploadImage(image, uuid);
                //do your upload image operations here, once done, call onImageUploadComplete and pass the url and uuid as reference.
                //editor.onImageUploadComplete("http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg",uuid);
                // editor.onImageUploadFailed(uuid);
            }
        });

    }

    private void uploadImage(final Bitmap image, final String uuid) {

        final Handler mHandler = new Handler();

        new Thread() {
            @Override
            public void run() {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 25, stream);
                final byte[] byteArray = stream.toByteArray();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        uploadMedia(byteArray, uuid);
                    }
                });

            }

        }.start();

    }


    private void uploadMedia(byte[] bytes, final String uuid) {

        Log.d("EditorView", "Uploading Media");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference ref =
                storageRef
                        .child("article_images")
                        .child(PostJobModel.getMediaLocation());

        final UploadTask uploadTask = ref.putBytes(bytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                editor.onImageUploadComplete(downloadUrl.toString(), uuid);
                if(onImageUploadListener!=null){
                    onImageUploadListener.onImageUploaded(downloadUrl.toString());
                }
            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                editor.onImageUploadFailed(uuid);
            }
        });
    }

    @Override
    public void onHeading1Active() {
        editor.updateTextStyle(EditorTextStyle.H1);
    }

    @Override
    public void onHeading2Active() {
        editor.updateTextStyle(EditorTextStyle.H2);
    }

    @Override
    public void onHeadingClear() {
        editor.updateTextStyle(EditorTextStyle.H1);
    }

    @Override
    public void onBoldText(boolean isBoldActive) {
        editor.updateTextStyle(EditorTextStyle.BOLD);
    }

    @Override
    public void onItalicText(boolean isActive) {
        editor.updateTextStyle(EditorTextStyle.ITALIC);
    }

    @Override
    public void onList(boolean isOrdered) {
        editor.insertList(isOrdered);
        //editor.getListItemExtensions().ConvertListToNormalText();
    }

    @Override
    public void onInsertLink() {
        // show dialog
        showInsertDialog();
    }

    private void showInsertDialog() {

        LinkInsertDialog linkInsertDialog = new LinkInsertDialog(mContext);

        linkInsertDialog.setOnLinkInsertedListener(new LinkInsertDialog.OnLinkInsertedListener() {
            @Override
            public void onLinkAdded(String link) {
                editor.insertLink(link);
            }
        });

        linkInsertDialog.show();

    }

    @Override
    public void onInsertDivider() {
        editor.insertDivider();
    }


    @Override
    public void onInsertImage() {
        editor.openImagePicker();
    }

    public Editor getEditor() {
        return editor;
    }

    private OnImageUploadListener onImageUploadListener;

    public void setOnImageUploadListener(OnImageUploadListener onImageUploadListener) {
        this.onImageUploadListener = onImageUploadListener;
    }

    public interface OnImageUploadListener{
        void onImageUploaded(String remotePath);
    }
}