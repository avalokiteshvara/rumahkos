@extends('admin.layout')
@section('content')

<!-- Content Wrapper. Contains page content -->

    <!-- Content Header (Page header) -->
    <div class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1 class="m-0 text-dark">Dashboard</h1>
          </div><!-- /.col -->
          <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
              <li class="breadcrumb-item"><a href="#">Home</a></li>
              <li class="breadcrumb-item active">Dashboard</li>
            </ol>
          </div><!-- /.col -->
        </div><!-- /.row -->
      </div><!-- /.container-fluid -->
    </div>
    <!-- /.content-header -->

    <!-- Main content -->
    <section class="content">
      <div class="container-fluid">
    
        <!-- Main row -->
        <div class="row">
          <!-- Left col -->
          <section class="col-lg-12 connectedSortable">
            <!-- Custom tabs (Charts with tabs)-->
            <div class="card">
              <div class="card-header">
                <h3 class="card-title">
                  <i class="fas fa-chart-pie mr-1"></i>
                  Peta sebaran Rumah Kos (tekan ctrl + mouse scrool untuk zoom)
                </h3>
               
              </div><!-- /.card-header -->
              <div class="card-body">
                <div class="tab-content p-0">
                <script type="text/javascript" src="https://maps.google.com/maps/api/js?key=AIzaSyAEny_-rEC4vWYBxlfham5LTtvKDuxF8BE"></script>
                    <div id="map" style="width: 100%; height: 500px;"></div>
                </div>
              </div><!-- /.card-body -->
            </div>
            <!-- /.card -->
            

            <script type="text/javascript">
              var locations = [  {!! $aset_location; !!} ];

              var map = new google.maps.Map(document.getElementById('map'), {
                zoom: 10,
                center: new google.maps.LatLng(-8.2325, 114.35755),
                mapTypeId: google.maps.MapTypeId.ROADMAP
              });

              var infowindow = new google.maps.InfoWindow();

              var marker, i;

              for (i = 0; i < locations.length; i++) {  
                marker = new google.maps.Marker({
                  position: new google.maps.LatLng(locations[i][1], locations[i][2]),
                  map: map
                });

                google.maps.event.addListener(marker, 'click', (function(marker, i) {
                  return function() {
                    infowindow.setContent(locations[i][0]);
                    infowindow.open(map, marker);
                  }
                })(marker, i));
              }


             
            </script>
            
          </section>
          <!-- /.Left col -->
          <!-- right col (We are only adding the ID to make the widgets sortable)-->
          
          <!-- right col -->
        </div>
        <!-- /.row (main row) -->
      </div><!-- /.container-fluid -->
    </section>
    <!-- /.content -->
  

@endsection