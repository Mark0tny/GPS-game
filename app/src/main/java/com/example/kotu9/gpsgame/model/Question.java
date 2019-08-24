package com.example.kotu9.gpsgame.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NonNull
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class Question implements Serializable, Parcelable {
	public String question;
	public Map<String, Boolean> answers = new HashMap<>();

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.question);
		dest.writeInt(this.answers.size());
		for (Map.Entry<String, Boolean> entry : this.answers.entrySet()) {
			dest.writeString(entry.getKey());
			dest.writeValue(entry.getValue());
		}
	}

	protected Question(Parcel in) {
		this.question = in.readString();
		int answersSize = in.readInt();
		this.answers = new HashMap<String, Boolean>(answersSize);
		for (int i = 0; i < answersSize; i++) {
			String key = in.readString();
			Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
			this.answers.put(key, value);
		}
	}

	public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
		@Override
		public Question createFromParcel(Parcel source) {
			return new Question(source);
		}

		@Override
		public Question[] newArray(int size) {
			return new Question[size];
		}
	};
}
