<template>
  <div>
    <header>
      <img
        class="logo"
        src="@/assets/sattva-logo.jpeg"
      />
      <div class="inner_header">
         <router-link style="text-decoration: none; color: inherit;" v-bind:to="{ name: 'home'}">

          <!-- <img class="logo" src="../../img/sattva-logo.jpeg" /> -->
          <div class="logo_container">
            <h1 class="title">Sattva Yoga Center</h1>
              </div>
          </router-link> 
        


      </div>
      
    </header>


  </div>
</template>

<script>
export default {
    name:  "header-logo",
    data(){
        return{
         
        }
    },
    created(){
      
    }
};
</script>

<style>
header {
  display: flex;
  justify-content: center;
}

.inner_header{
  margin-left: 40px;
}

header {
  /* display: flex;
  
  text-align: center;
  background-color: #167fa5;
  /* background-color:aliceblue; 
  border-bottom: 1px solid black;
  box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;
 
  width: 80%;
  height: 80px; */
}



.logo_container{
  /* height: 100%;
  display: table;
  float: left; */
}

.logo_container h1{
  /* font-family: 'Billabong', sans-serif;
  /* font-family: 'Montserrat', sans-serif; 
  color: aliceblue;
  height: 100%;
  display: table-cell;
  vertical-align: middle;
  font-size: 55px;
  font-weight: 200; */
}

.logo{
  /* height: 100%;
  display: table-cell;

  vertical-align: middle;
  float:left;
  padding: 0px 0px 0px 10px;

  margin-top: 10px;
  margin-bottom: auto;

  width: 60px;*/
  height: 130px; 

}


</style>