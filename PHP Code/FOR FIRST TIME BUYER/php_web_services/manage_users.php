<?php $page_title="Manage Users";

include('includes/header.php'); 
include('language/language.php');  
include("includes/function.php");

$tableName="tbl_users";   
$targetpage = "manage_users.php"; 
$limit = 15; 

$keyword='';

if(!isset($_GET['keyword'])){
  $query = "SELECT COUNT(*) as num FROM $tableName";
}
else{

  $keyword=addslashes(trim($_GET['keyword']));

  $query = "SELECT COUNT(*) as num FROM $tableName WHERE (`name` LIKE '%$keyword%' OR `email` LIKE '%$keyword%' OR `phone` LIKE '%$keyword%') AND id <> 0";

  $targetpage = "manage_users.php?keyword=".$_GET['keyword'];

}

$total_pages = mysqli_fetch_array(mysqli_query($mysqli,$query));
$total_pages = $total_pages['num'];

$stages = 3;
$page=0;
if(isset($_GET['page'])){
  $page = mysqli_real_escape_string($mysqli,$_GET['page']);
}
if($page){
  $start = ($page - 1) * $limit; 
}else{
  $start = 0; 
} 

if(!isset($_GET['keyword'])){
  $sql_query="SELECT * FROM tbl_users WHERE  id <> 0 ORDER BY tbl_users.`id` DESC LIMIT $start, $limit"; 
}
else{

  $sql_query="SELECT * FROM tbl_users WHERE (`name` LIKE '%$keyword%' OR `email` LIKE '%$keyword%' OR `phone` LIKE '%$keyword%') AND  id <> 0 ORDER BY tbl_users.`id` DESC LIMIT $start, $limit"; 
}

$result=mysqli_query($mysqli,$sql_query) or die(mysqli_error($mysqli));



function highlightWords($text, $word){
 $text = preg_replace('#'. preg_quote($word) .'#i', '<span style="background-color: #F9F902;">\\0</span>', $text);
 return $text;
}

?>
<div class="row">
  <div class="col-xs-12">
    <?php
    if(isset($_SERVER['HTTP_REFERER']))
    {
      echo '<a href="'.$_SERVER['HTTP_REFERER'].'"><h4 class="pull-left" style="font-size: 20px;color: #e91e63"><i class="fa fa-arrow-left"></i> Back</h4></a>';
    }
    ?>
    <div class="card mrg_bottom">
      <div class="page_title_block">
        <div class="col-md-5 col-xs-12">
          <div class="page_title"><?=$page_title?></div>
        </div>
        <div class="col-md-7 col-xs-12">
          <div class="search_list">
            <div class="search_block">
              <form method="get" action="">
                <input class="form-control input-sm" placeholder="Search..." aria-controls="DataTables_Table_0" type="search" name="keyword" value="<?php if(isset($_POST['keyword'])){ echo $_POST['keyword']; } ?>" required>
                <button type="submit" class="btn-search"><i class="fa fa-search"></i></button>
              </form>
            </div>
            <div class="add_btn_primary"> <a href="add_user.php?add">Add User</a> </div>
          </div>
        </div>
        <div class="col-md-4 col-xs-12 text-right" style="float: right;">
          <div class="checkbox" style="width: 95px;margin-top: 5px;margin-left: 10px;right: 100px;position: absolute;">
            <input type="checkbox" id="checkall_input">
            <label for="checkall_input">
              Select All
            </label>
          </div>
          <div class="dropdown" style="float:right">
            <button class="btn btn-primary dropdown-toggle btn_cust" type="button" data-toggle="dropdown">Action
              <span class="caret"></span></button>
              <ul class="dropdown-menu" style="right:0;left:auto;">
                <li><a href="javascript:void(0)" class="actions" data-action="enable" data-table="tbl_users" data-column="status">Enable</a></li>
                <li><a href="javascript:void(0)" class="actions" data-action="disable" data-table="tbl_users" data-column="status">Disable</a></li>
                <li><a href="javascript:void(0)" class="actions" data-action="delete" data-table="tbl_users" data-column="status">Delete !</a></li>
              </ul>
            </div>
          </div>
        </div>
        <div class="clearfix"></div>
        <div class="col-md-12 mrg-top">
          <table class="table table-striped table-bordered table-hover">
            <thead>
              <tr>
                <th></th>
                <th>User Type</th>
                <th>Device ID</th>
                <th>Name</th>
                <th>Email/Google/Facebook ID</th>
                <th>Phone</th>
                <th>Status</th>
                <th class="cat_action_list" style="width:200px">Action</th>
              </tr>
            </thead>
            <tbody>
              <?php           
              $i=0;
              if(mysqli_num_rows($result) > 0)
              {
              while($users_row=mysqli_fetch_array($result))
              {
               $device_id = !empty($keyword)?highlightWords($users_row['device_id'], $keyword):$users_row['device_id'];
               $name = !empty($keyword)?highlightWords($users_row['name'], $keyword):$users_row['name'];

               if($users_row['email']!='' AND $users_row['user_type']=='Normal')
               {
                $email = !empty($keyword)?highlightWords($users_row['email'], $keyword):$users_row['email'];	
              }
              else if($users_row['user_type']=='Google'){
                if($users_row['user_type']=='Google' AND $users_row['email']=='' AND $users_row['auth_id']!=''){
                 $email = !empty($keyword)?highlightWords($users_row['auth_id'], $keyword):$users_row['auth_id'];
               }
               else{
                 $email = !empty($keyword) ? highlightWords($users_row['email'], $keyword):$users_row['email'];
               }

             }
             else if($users_row['user_type']=='Facebook'){
              if($users_row['user_type']=='Facebook' AND $users_row['email']=='' AND $users_row['auth_id']!=''){
               $email = !empty($keyword)?highlightWords($users_row['auth_id'], $keyword):$users_row['auth_id'];
             }
             else{
               $email = !empty($keyword)?highlightWords($users_row['email'], $keyword):$users_row['email'];
             }

           } 
           ?>
           <tr <?php if($users_row['is_duplicate']==1){ echo 'style="background-color: rgba(255,0,0,0.1);"'; } ?>>
            <td nowrap="">
              <div class="checkbox" style="margin: 0px;float: left;">
                <input type="checkbox" name="post_ids[]" id="checkbox<?php echo $i; ?>" value="<?php echo $users_row['id']; ?>" class="post_ids">
                <label for="checkbox<?php echo $i; ?>">
                </label>
              </div>
            </td>
            <td><?php echo $users_row['user_type']; ?></td>
            <td><?php echo $device_id;?></td>
            <td><a href="user_profile.php?user_id=<?= $users_row['id'] ?>">
             <?php echo $users_row['name'];?> </a></td>
             <td><?php echo $users_row['email']; ?></td>
             <td><?php echo $users_row['phone']; ?></td>
             <td>
               <div class="row toggle_btn">
                <input type="checkbox" id="enable_disable_check_<?=$i?>" data-id="<?=$users_row['id']?>" data-table="tbl_users" data-column="status" class="cbx hidden enable_disable" <?php if($users_row['status']==1){ echo 'checked';} ?>>
                <label for="enable_disable_check_<?=$i?>" class="lbl"></label>
              </div>

            </td>
            <td>
             <a href="user_profile.php?user_id=<?=$users_row['id']?>" data-id="<?php echo $users_row['id'];?>" class="btn btn-success btn_edit" data-toggle="tooltip" data-tooltip="Profile"><i class=" fa fa-history"></i></a>
             <a href="add_user.php?user_id=<?php echo $users_row['id']; ?>" class="btn btn-primary btn_cust" data-toggle="tooltip" data-tooltip="Edit"><i class="fa fa-edit"></i></a>
             <a href="javascript:void(0)" data-table="tbl_users" data-id="<?php echo $users_row['id']; ?>" data-toggle="tooltip" data-tooltip="Delete" class="btn btn-danger btn_delete btn_cust"><i class="fa fa-trash"></i></a>
           </td>
         </tr>
         <?php $i++;
       }
     }else{
      ?>
      <tr>
        <td colspan="8">
          <p class="not_data"><strong>Sorry</strong> No data found!</p>
        </td>
      </tr>
      <?php } ?>
     </tbody>
   </table>
 </div>
 <div class="col-md-12 col-xs-12">
  <div class="pagination_item_block">
    <nav>
      <?php if (!isset($_POST["user_search"])) {
        include("pagination.php");
      } ?>
    </nav>
  </div>
</div>
<div class="clearfix"></div>
</div>
</div>
</div>

<?php include('includes/footer.php');?>                  


