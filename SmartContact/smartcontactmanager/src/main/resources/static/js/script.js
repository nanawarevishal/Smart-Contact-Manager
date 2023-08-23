

console.log("testing")


function toggleSidebar() {
    
    const sidebar =  document.getElementsByClassName("sidebar")[0];
    const content =  document.getElementsByClassName("content")[0];

    if(window.getComputedStyle(sidebar).display === "none"){
        sidebar.style.display = "block";
        content.style.marginLeft = "20%";
    }
    else{
        sidebar.style.display = "none";
        content.style.marginLeft = "0%";
    }
}


const search=()=>{
    
    let query = $("#search-input").val();
    
    if(query==""){
        $(".search-result").hide();
    }
    
    else{
        
        console.log(query);

        // sending request to server

        let url = `http://127.0.0.1:8080/search/${query}`;

        fetch(url)
            .then((response)=>{
                return response.json();
            })

            .then((data)=>{

                // data.
                // console.log(data);

                let text = `<div class='list-group'>`

                data.forEach((contact)=>{

                    text +=`<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'> ${contact.name} </a>`
                });

                text+=`</div>`

                $(".search-result").html(text);
                $(".search-result").show();

            });


    }
}


// first request to server to create order

const paymentStart=()=>{
    console.log("Paymner Started...!");

    let amount = $("#payment_field").val()
    console.log(amount);

    if(amount=='' || amount == null){
        swal("Failed...! ", "Amount is required ..!", "error");

        return;
    }

    // code..
    // we will use ajax to send request to server to send order

    $.ajax(

        {
            url : '/user/create_order',
            data :JSON.stringify({
                amount : amount,
                info :'order_request',
            }),

            contentType : 'application/json',
            type :'POST',
            dataType :'json',

            success : function(response){

                // this function invoked when success
                console.log(response)

                if(response.status == "created"){
                    // Open payment form

                    let options={
                        key:'rzp_test_HEXkABQCzh5zDJ',
                        amount :response.amount,
                        currency :'INR',
                        name:'iCoder Smart Contact Manager',
                        description:"Donation",
                        image:"https://cdn.pixabay.com/photo/2020/07/01/12/58/icon-5359553_1280.png",
                        order_id:response.id,
                        handler:function(response){
                            console.log(response.razorpay_payment_id)
                            console.log(response.razorpay_order_id)
                            console.log(response.razorpay_signature)
                            console.log("")
                            swal("Good job!", "payment successful ..!", "success");
                        },

                        "prefill": { 
                            "name": "", 
                            "email": "",
                            "contact": "" 
                        },

                        "notes": {
                            "address": "iCoder Corporate Office"
                        },

                        "theme": {
                            "color": "#3399cc"
                        },

                    };

                    let rzp = new Razorpay(options);
                    rzp.on('payment.failed', function (response){
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    alert("Ohhh..! payment failed..!")
                    swal("Something went wrong!", "Oops Payment failed ..!", "error");

                    });

                    rzp.open();

                }
            },

            error :function(error){

                // invoke when error
                console.log(error)
                alert("Something went wrong...!")
            }

        }
    )
}

