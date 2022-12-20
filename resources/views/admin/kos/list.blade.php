@inject('helper', 'App\Helpers\HelpersFunction')
@extends('admin.layout')
@section('content')
<!-- Content Header (Page header) -->
<section class="content-header">
   <div class="container-fluid">
      <div class="row mb-2">
         <div class="col-sm-6">
            <h1>Data Rumah Kos</h1>
         </div>
         <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
               <li class="breadcrumb-item"><a href="{{ URL::to('/admin') }}">Home</a></li>
               <li class="breadcrumb-item active">Data Rumah Kos</li>
            </ol>
         </div>
      </div>
   </div>
   <!-- /.container-fluid -->
</section>
<section class="content">
   <div class="container-fluid">
      <div class="row">
         <div class="col-12">
            <div class="card">
               <div class="card-header container-fluid">
                  <div class="row">
                     <div class="col-md-10">
                        <h3 class="card-title">Data Rumah Kos</h3>
                     </div>
                     <div class="col-md-2">
                        <!-- <a href="{{ URL::to('/pemilik/rumah-kos-tambah') }}" class="btn btn-primary float-right">Tambah Data</a>               -->
                     </div>
                  </div>
               </div>
               <!-- /.card-header -->
               <div class="card-body">
                  <table id="dataTables" class="table table-bordered table-striped">
                     <thead>
                        <tr>
                           <th>Nama Rumah Kos</th>
                           <th>Alamat</th>
                           <th>Tipe</th>
                           <th>Kamar</th>
                           <th>Harga Sewa @bulan</th>                           
                           <!-- <th></th> -->
                        </tr>
                     </thead>
                     <tbody>
                        @foreach($data as $d)
                        <tr>
                           <td>{{ $d->nama }}</td>
                           <td>{!! $d->alamat . '<br/> (' . $d->kecamatan . ' - ' . $d->kelurahan . ')' !!}</td>
                           <td>{!! ucfirst($d->tipe) !!}</td>
                           <td>{!! ($d->kmr_terisi + $d->kmr_tersisa) . ' Kamar (' . $d->kmr_terisi . '&nbsp;Terisi)' !!}</td>
                           <td>{!! $helper->rupiah($d->harga_sewa) !!}</td>
                           
                           <!-- <td>                               -->
                              <!-- <a href="{!! URL::to('/pemilik/rumah-kos-edit/' . sha1($d->id)) !!}" class="btn btn-warning btn-xs">Edit</a>
                              <a href="{!! URL::to('/pemilik/rumah-kos-hapus/' . sha1($d->id)) !!}" class="btn btn-danger btn-xs">Hapus</a>
                              <a href="{!! URL::to('/pemilik/rumah-kos-penyewa/' . sha1($d->id)) !!}" class="btn btn-success btn-xs">Penyewa</a>                               -->
                           <!-- </td> -->
                        </tr>
                        @endforeach
                     </tbody>
                  </table>
               </div>
               <!-- /.card-body -->
            </div>
            <!-- /.card -->
         </div>
      </div>
   </div>
</section>
<script>
   $(function () {
     
     $('#dataTables').DataTable({
       "paging": true,
       "lengthChange": false,
       "searching": true,
       "ordering": true,
       "info": true,
       "autoWidth": false,
       "responsive": true,
     });
   });
</script>
@endsection