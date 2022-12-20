<!DOCTYPE html>
<!--
   This is a starter template page. Use this page to start your new project from
   scratch. This page gets rid of all links and provides the needed markup only.
   -->
<html lang="en">
   <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <meta http-equiv="x-ua-compatible" content="ie=edge">
      <title>PEMILIK | KAMARKOS</title>
      <!-- Font Awesome Icons -->
      <link rel="stylesheet" href="{{ URL::to('adminlte-3.0.5/plugins/fontawesome-free/css/all.min.css') }}">
      <!-- DataTables -->
      <link rel="stylesheet" href="{{ URL::to('adminlte-3.0.5/plugins/datatables-bs4/css/dataTables.bootstrap4.min.css') }}">
      <link rel="stylesheet" href="{{ URL::to('adminlte-3.0.5/plugins/datatables-responsive/css/responsive.bootstrap4.min.css') }}">
      <!-- Theme style -->
      <link rel="stylesheet" href="{{ URL::to('adminlte-3.0.5/dist/css/adminlte.min.css') }}">
      <!-- <link rel="stylesheet" href="{{ URL::to('adminlte-3.0.5/dist/css/skins/_all-skins.min.css') }}"> -->

      <!-- Google Font: Source Sans Pro -->
      <link href="https://fonts.googleapis.com/css?family=Source+Sans+Pro:300,400,400i,700" rel="stylesheet">
      
       <!-- Tempusdominus Bbootstrap 4 -->
      <link rel="stylesheet" href="{{ URL::to('adminlte-3.0.5/plugins/tempusdominus-bootstrap-4/css/tempusdominus-bootstrap-4.min.css') }}">

      <!-- Select2 -->
      <link rel="stylesheet" href="{{ URL::to('adminlte-3.0.5/plugins/select2/css/select2.min.css') }}">
      <link rel="stylesheet" href="{{ URL::to('adminlte-3.0.5/plugins/select2-bootstrap4-theme/select2-bootstrap4.min.css') }}">

      <!-- REQUIRED SCRIPTS -->
      <!-- jQuery -->
      <script src="{{ URL::to('adminlte-3.0.5/plugins/jquery/jquery.min.js') }}"></script>
      <!-- Bootstrap 4 -->
      <script src="{{ URL::to('adminlte-3.0.5/plugins/bootstrap/js/bootstrap.bundle.min.js') }}"></script>
      <script src="{{ URL::to('adminlte-3.0.5/plugins/moment/moment.min.js') }}"></script>
      <!-- DataTables -->
      <script src="{{ URL::to('adminlte-3.0.5/plugins/datatables/jquery.dataTables.min.js') }}"></script>
      <script src="{{ URL::to('adminlte-3.0.5/plugins/datatables-bs4/js/dataTables.bootstrap4.min.js') }}"></script>
      <script src="{{ URL::to('adminlte-3.0.5/plugins/datatables-responsive/js/dataTables.responsive.min.js') }}"></script>
      <script src="{{ URL::to('adminlte-3.0.5/plugins/datatables-responsive/js/responsive.bootstrap4.min.js') }}"></script>
      <!-- Tempusdominus Bootstrap 4 -->
      <script src="{{ URL::to('adminlte-3.0.5/plugins/tempusdominus-bootstrap-4/js/tempusdominus-bootstrap-4.min.js') }}"></script>

      <!-- Select2 -->
      <script src="{{ URL::to('adminlte-3.0.5/plugins/select2/js/select2.full.min.js') }}"></script>

      <!-- AdminLTE App -->
      <script src="{{ URL::to('adminlte-3.0.5/dist/js/adminlte.min.js') }}"></script>
      
   </head>
   <body class="hold-transition sidebar-mini">
    <div class="content-wrapper">
        @yield('content')
    </div>
   </body>
   
</html>