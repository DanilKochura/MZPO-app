import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Prices (

	@SerializedName("sale15") val sale15 : Int?,
	@SerializedName("ind") val ind : Int?,
	@SerializedName("weekend") val weekend : Int?,
	@SerializedName("dist") val dist : Int?,
	@SerializedName("intensive") val intensive : Int?
)

data class NewPrices (

	@SerializedName("sale15") val sale15 : NewPrice?,
	@SerializedName("ind") val ind : NewPrice?,
	@SerializedName("weekend") val weekend : NewPrice?,
	@SerializedName("dist") val dist : NewPrice?,
	@SerializedName("intensive") val intensive : NewPrice?
)
data class NewPrice (
	@SerializedName("old") val old : Int?,
	@SerializedName("price") val price : Int?,
)

